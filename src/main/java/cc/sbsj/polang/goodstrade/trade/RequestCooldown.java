package cc.sbsj.polang.goodstrade.trade;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongSupplier;

/** 按发起者全局计算的发送冷却，独立于请求接收者和请求有效期。 */
public final class RequestCooldown {
    private final Map<UUID, Long> deadlines = new HashMap<>();
    private final LongSupplier clock;

    public RequestCooldown() { this(() -> System.nanoTime() / 1_000_000L); }
    RequestCooldown(LongSupplier clock) { this.clock = clock; }

    public long remaining(UUID sender) {
        Long deadline = deadlines.get(sender);
        return deadline == null ? 0 : Math.max(0, deadline - clock.getAsLong());
    }

    public void record(UUID sender, long durationMillis) {
        if (durationMillis > 0) deadlines.put(sender, clock.getAsLong() + durationMillis);
    }

    public void cleanup() {
        long now = clock.getAsLong();
        deadlines.values().removeIf(deadline -> deadline <= now);
    }

    public void clear() { deadlines.clear(); }
}
