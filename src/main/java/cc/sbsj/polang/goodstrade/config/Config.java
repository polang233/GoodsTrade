package cc.sbsj.polang.goodstrade.config;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import org.bukkit.configuration.Configuration;

public class Config {
    public Configuration config;
    public GoodsTrade plugin;

    public Config(GoodsTrade plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
    }

    public int getWaitTime() {
//        config.getInt("Trade.Wait-Time", 5)
        //验证下有没有过64，省的爆掉了物品堆叠
        int waitTime = config.getInt("Trade.Wait-Time");
        if (waitTime > 64) {
            waitTime = 64;
        }
        return waitTime;
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public boolean isEnabledShiftClick() {
        return config.getBoolean("Trade.Triggers.Shift-Right-Click", false);
    }

    public boolean isSafeDamage() {
        return config.getBoolean("Trade.Safe.Damage", false);
    }

    public boolean isSafeMove() {
        return config.getBoolean("Trade.Safe.Move", false);
    }
}
