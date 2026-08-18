package cc.sbsj.polang.goodstrade.compat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * Spigot 的 BungeeChat 消息适配。
 */
public final class BungeeMessageSender implements ClickMessageSender {
    public BungeeMessageSender() {
    }

    @Override
    public boolean send(Player player, String message, String command, String hoverMessage) {
        BaseComponent component = new TextComponent(message);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        component.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(hoverMessage)
        ));

        try {
            // 1.12 Spigot/CatServer 使用 Player.Spigot 的发送接口。
            player.spigot().sendMessage(component);
            return true;
        } catch (LinkageError ignored) {
            // 由兼容入口切换为普通文本发送器，后续不再重复触发异常。
            return false;
        }
    }
}
