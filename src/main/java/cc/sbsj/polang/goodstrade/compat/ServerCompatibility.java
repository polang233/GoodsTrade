package cc.sbsj.polang.goodstrade.compat;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.compat.paper.PaperSecurityEvents;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端能力兼容入口。
 * 主业务代码只调用这里，不直接依赖 Paper、Adventure 等可选 API。
 */
public final class ServerCompatibility {
    private static final String BUNGEE_TEXT_COMPONENT = "net.md_5.bungee.api.chat.TextComponent";
    private static final String BUNGEE_MESSAGE_SENDER =
            "cc.sbsj.polang.goodstrade.compat.BungeeMessageSender";
    private static final String PAPER_TAB_COMPLETE_EVENT =
            "com.destroystokyo.paper.event.server.AsyncTabCompleteEvent";
    private static final String FOLIA_SERVER =
            "io.papermc.paper.threadedregions.RegionizedServer";
    private static final ClickMessageSender PLAIN_TEXT_MESSAGE_SENDER =
            (player, message, command, hoverMessage) -> {
                player.sendMessage(message);
                return true;
            };
    private static final Set<UUID> PLUGIN_CLOSED_INVENTORIES =
            Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());
    private static volatile ClickMessageSender clickMessageSender = PLAIN_TEXT_MESSAGE_SENDER;
    private static volatile Boolean folia;

    private ServerCompatibility() {
    }

    /**
     * 发送启动时已选定的消息策略，不在交易过程中反射。
     */
    public static void sendClickableMessage(Player player, String message, String command, String hoverMessage) {
        if (!clickMessageSender.send(player, message, command, hoverMessage)) {
            clickMessageSender = PLAIN_TEXT_MESSAGE_SENDER;
            PLAIN_TEXT_MESSAGE_SENDER.send(player, message, command, hoverMessage);
        }
    }

    /**
     * 使用 Bukkit 的旧接口踢人，避免依赖 Adventure 的 Component。
     */
    public static void kickPlayer(Player player, String message) {
        player.kickPlayer(message);
    }

    /**
     * 使用所有版本都有的 closeInventory()，并标记本次关闭来自插件。
     */
    public static void closeInventory(Player player) {
        UUID playerId = player.getUniqueId();
        PLUGIN_CLOSED_INVENTORIES.add(playerId);
        try {
            player.closeInventory();
        } finally {
            PLUGIN_CLOSED_INVENTORIES.remove(playerId);
        }
    }

    /**
     * 判断关闭事件是否由 {@link #closeInventory(Player)} 触发。
     * 只查询标记，不在事件里消费；清理由 {@link #closeInventory(Player)} 的 finally 负责。
     */
    public static boolean isPluginInventoryClose(InventoryCloseEvent event) {
        return PLUGIN_CLOSED_INVENTORIES.contains(event.getPlayer().getUniqueId());
    }

    /**
     * 是否运行在 Folia 服务端。
     */
    public static boolean isFolia() {
        Boolean cached = folia;
        if (cached != null) {
            return cached;
        }
        boolean detected = isClassAvailable(FOLIA_SERVER, ServerCompatibility.class.getClassLoader());
        folia = detected;
        return detected;
    }

    /**
     * 插件启动时检测并缓存服务端能力。
     */
    public static void initialize(GoodsTrade plugin) {
        initializeClickMessageSender();
        registerPaperSecurityListener(plugin);
    }

    /**
     * 注册可选的 Paper 监听器。非 Paper 服务端会自动跳过。
     */
    private static void registerPaperSecurityListener(GoodsTrade plugin) {
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        if (!isClassAvailable(PAPER_TAB_COMPLETE_EVENT, classLoader)) {
            plugin.getLogger().info("Paper安全检测未启用");
            return;
        }

        try {
            new PaperSecurityEvents(plugin).register();
            plugin.getLogger().info("Paper安全检测已启用");
        } catch (ClassNotFoundException | LinkageError | ClassCastException ignored) {
            plugin.getLogger().warning("Paper安全检测不可用，已跳过");
        }
    }

    private static void initializeClickMessageSender() {
        clickMessageSender = PLAIN_TEXT_MESSAGE_SENDER;
        ClassLoader classLoader = ServerCompatibility.class.getClassLoader();
        if (!isClassAvailable(BUNGEE_TEXT_COMPONENT, classLoader)) {
            return;
        }

        try {
            Class<?> senderClass = Class.forName(BUNGEE_MESSAGE_SENDER, true, classLoader);
            Object sender = senderClass.getDeclaredConstructor().newInstance();
            if (sender instanceof ClickMessageSender) {
                clickMessageSender = (ClickMessageSender) sender;
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // 保留默认的普通文本发送器。
        }
    }

    private static boolean isClassAvailable(String className, ClassLoader classLoader) {
        try {
            Class.forName(className, false, classLoader);
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }
}
