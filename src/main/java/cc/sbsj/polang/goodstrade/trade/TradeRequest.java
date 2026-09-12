package cc.sbsj.polang.goodstrade.trade;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TradeRequest {
    /** 获取发起者 ID */
    @Getter
    private final UUID senderId;
    /** 获取目标玩家 ID */
    @Getter
    private final UUID targetId;
    /** 获取发起者名字 */
    @Getter
    private final String senderName;
    /** 获取请求创建时间 */
    @Getter
    private final long timestamp;
    private final long expiryMillis; // 请求有效期（毫秒）

    public TradeRequest(Player sender, Player target, long expiryMillis) {
        this(sender, target, expiryMillis, System.currentTimeMillis());
    }

    TradeRequest(Player sender, Player target, long expiryMillis, long timestamp) {
        this.senderId = sender.getUniqueId();
        this.targetId = target.getUniqueId();
        this.senderName = sender.getName();
        this.timestamp = timestamp;
        this.expiryMillis = expiryMillis;
    }

    /**
     * 检查请求是否已过期
     */
    public boolean isExpired() {
        return isExpired(System.currentTimeMillis());
    }

    boolean isExpired(long now) {
        return now - timestamp >= expiryMillis;
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
     * 获取请求剩余有效期（毫秒）；保留旧方法名以兼容调用方
     */
    public long getRemainingCooldown() {
        long elapsed = System.currentTimeMillis() - timestamp;
        return Math.max(0, expiryMillis - elapsed);
    }
}
