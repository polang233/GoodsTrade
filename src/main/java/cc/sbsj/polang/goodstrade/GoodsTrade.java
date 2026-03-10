package cc.sbsj.polang.goodstrade;

import cc.sbsj.polang.goodstrade.commands.GoodsTradeCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class GoodsTrade extends JavaPlugin {
    public static final String PREFIX = "§7[§2§lGoods§6§lTrade§7] §r";
    public static GoodsTrade instance;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("goodstrade").setExecutor(new GoodsTradeCommand(this));

    }

    @Override
    public void onDisable() {
    }
}
