package cc.sbsj.polang.goodstrade;

import cc.sbsj.polang.goodstrade.commands.GoodsTradeCommand;
import cc.sbsj.polang.goodstrade.config.Config;
import cc.sbsj.polang.goodstrade.hook.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class GoodsTrade extends JavaPlugin {
    public static final String PREFIX = "§7[§2§lGoods§6§lTrade§7] §r";
    public static GoodsTrade instance;
    public Metrics metrics;
    public static Config config;

    @Override
    public void onEnable() {
        instance = this;
        config = new Config(this);
        metrics = new Metrics(this, 30110);
        getLogger().info(PREFIX + "§3插件版本: §bv" + this.getDescription().getVersion());
        getLogger().info(PREFIX + "§3插件功能: §e" + this.getDescription().getDescription());

        getCommand("goodstrade").setExecutor(new GoodsTradeCommand(this));
        getLogger().info("§2命令成功加载");
        getServer().getPluginManager().registerEvents(new Events(), this);
        getLogger().info("§2事件监听器成功注册");

        getLogger().info(PREFIX + "§a成功加载了喵~");
    }

    @Override
    public void onDisable() {
        getLogger().info("§a插件卸载了喵~");
        metrics.shutdown();
    }
}
