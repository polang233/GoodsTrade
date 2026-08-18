package cc.sbsj.polang.goodstrade.compat.paper;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.compat.ServerCompatibility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;

/**
 * Paper 异步补全安全监听。
 * 使用反射注册事件，因此本类可以留在主源码并兼容不带 Paper API 的服务端。
 */
public final class PaperSecurityEvents implements Listener, EventExecutor {
    private static final String PAPER_TAB_COMPLETE_EVENT =
            "com.destroystokyo.paper.event.server.AsyncTabCompleteEvent";

    private final GoodsTrade plugin;
    private Method getBufferMethod;
    private Method getSenderMethod;

    public PaperSecurityEvents(GoodsTrade plugin) {
        this.plugin = plugin;
    }

    /**
     * 在确认 Paper 事件类存在后注册监听器。
     */
    @SuppressWarnings("unchecked")
    public void register() throws ClassNotFoundException {
        Class<?> eventClass = Class.forName(PAPER_TAB_COMPLETE_EVENT, false, plugin.getClass().getClassLoader());
        if (!Event.class.isAssignableFrom(eventClass)) {
            throw new ClassCastException(PAPER_TAB_COMPLETE_EVENT + " is not a Bukkit event");
        }
        try {
            getBufferMethod = eventClass.getMethod("getBuffer");
            getSenderMethod = eventClass.getMethod("getSender");
        } catch (NoSuchMethodException e) {
            throw new ClassNotFoundException("Paper tab-complete event methods are unavailable", e);
        }

        Bukkit.getPluginManager().registerEvent(
                (Class<? extends Event>) eventClass,
                this,
                EventPriority.NORMAL,
                this,
                plugin,
                true
        );
    }

    @Override
    public void execute(Listener ignored, Event event) throws EventException {
        try {
            String buffer = (String) getBufferMethod.invoke(event);
            if (buffer == null || !buffer.contains("@") || !buffer.contains("[nbt=")) {
                return;
            }

            if (event instanceof Cancellable) {
                ((Cancellable) event).setCancelled(true);
            }

            Object sender = getSenderMethod.invoke(event);
            if (!(sender instanceof Player)) {
                return;
            }

            Player player = (Player) sender;
            Bukkit.getScheduler().runTask(plugin, () ->
                    // 使用兼容层的旧版踢人接口，避免 Adventure Component 依赖。
                    ServerCompatibility.kickPlayer(player, GoodsTrade.getPrefix()
                            + GoodsTrade.lang.getString("security.exploit-attempt")));
        } catch (ReflectiveOperationException ignoredException) {
            // 服务端的同名事件签名不兼容时，跳过本次检测，避免影响主功能。
        }
    }
}
