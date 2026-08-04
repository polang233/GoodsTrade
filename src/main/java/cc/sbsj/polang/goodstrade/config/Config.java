package cc.sbsj.polang.goodstrade.config;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.util.ItemBlackList;
import org.bukkit.configuration.Configuration;

public class Config {
    private Configuration config;
    private ItemBlackList itemBlackList;

    public Config(GoodsTrade plugin) {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        loadItemBlackList();
    }

    public int getWaitTime() {
        //验证下有没有过64，省的爆掉了物品堆叠
        int waitTime = config.getInt("Trade.Wait-Time", 5);
        if (waitTime > 64) {
            waitTime = 64;
        }
        return waitTime;
    }

    public void reload() {
        GoodsTrade.instance.reloadConfig();
        config = GoodsTrade.instance.getConfig();
        GoodsTrade.lang.load();
        ViewConfig.load(GoodsTrade.instance);
        loadItemBlackList();
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

    public ItemBlackList getItemBlackList() {
        return itemBlackList;
    }

    private void loadItemBlackList() {
        itemBlackList = ItemBlackList.fromConfig(config);
    }
}
