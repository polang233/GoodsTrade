package cc.sbsj.polang.goodstrade.trade;

import org.bukkit.Location;
import org.bukkit.configuration.Configuration;

import java.util.Collections;
import java.util.List;

/** 发起请求、打开界面和交易期间共用的位置限制规则。 */
public final class TradeRestrictions {
    private TradeRestrictions() { }

    public static boolean isWorldEnabled(Configuration config, String name) {
        List<String> worlds = config.isList("Trade.Enabled-Worlds")
                ? config.getStringList("Trade.Enabled-Worlds")
                : Collections.singletonList("*");
        return worlds.contains("*") || worlds.contains(name);
    }

    /** 位置不符合限制时返回提示的语言键，符合时返回 null。 */
    public static String check(Configuration config, Location sender, Location target, boolean active) {
        if (sender.getWorld() == null || target.getWorld() == null
                || !isWorldEnabled(config, sender.getWorld().getName())
                || !isWorldEnabled(config, target.getWorld().getName())) {
            return "trade-status.world-disabled";
        }
        boolean sameWorld = sender.getWorld().equals(target.getWorld());
        double limit = config.getDouble(active ? "Trade.Distance.Trading" : "Trade.Distance.Start", active ? 8 : 4);
        if (!Double.isFinite(limit) || limit < 0) limit = active ? 8 : 4;
        // 不同世界之间无法计算有效的空间距离。
        if (!sameWorld) {
            return config.getBoolean("Trade.Distance.Same-World", true) || limit > 0
                    ? "trade-status.same-world-required" : null;
        }
        if (limit > 0 && sender.distanceSquared(target) > limit * limit) {
            return active ? "trade-status.cancelled-by-distance" : "trade-status.start-too-far";
        }
        return null;
    }
}
