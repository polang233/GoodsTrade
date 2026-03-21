package cc.sbsj.polang.goodstrade.trade;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TradeRequest {
    /**
     * 发起者 ID
     */
    @Getter
    private final UUID senderId;
    /**
     * 目标玩家 ID
     */
    @Getter
    private final UUID targetId;
    /**
     * 发起者名字
     */
    @Getter
    private final String senderName;
    /**
     * 请求创建时间
     */
    @Getter
    private final long timestamp;
    private final long cooldown; // 冷却时间（毫秒）

    public TradeRequest(Player sender, Player target, long cooldown) {
        this.senderId = sender.getUniqueId();
        this.targetId = target.getUniqueId();
        this.senderName = sender.getName();
        this.timestamp = System.currentTimeMillis();
        this.cooldown = cooldown;
    }

    /**
     * 检查请求是否已过期
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > cooldown;
    }

    /**
     * 检查是否是同一个发起者
     */
    public boolean isSameSender(Player player) {
        return this.senderId.equals(player.getUniqueId());
    }

    /**
     * 检查是否是同一个目标玩家
     */
    public boolean isSameTarget(Player player) {
        return this.targetId.equals(player.getUniqueId());
    }

    /**
     * 获取剩余冷却时间（毫秒）
     */
    public long getRemainingCooldown() {
        long elapsed = System.currentTimeMillis() - timestamp;
        return Math.max(0, cooldown - elapsed);
    }
}
