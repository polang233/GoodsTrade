package cc.sbsj.polang.goodstrade.compat;

import org.bukkit.entity.Player;

/**
 * 交易请求消息发送策略。
 */
interface ClickMessageSender {
    /**
     * @return 是否成功使用当前策略发送。
     */
    boolean send(Player player, String message, String command, String hoverMessage);
}
