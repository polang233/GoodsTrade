package cc.sbsj.polang.goodstrade;

import cc.sbsj.polang.goodstrade.commands.GoodsTradeCommand;
import cc.sbsj.polang.goodstrade.config.Config;
import cc.sbsj.polang.goodstrade.config.Lang;
import cc.sbsj.polang.goodstrade.config.PlayerDataManager;
import cc.sbsj.polang.goodstrade.config.ViewConfig;
import cc.sbsj.polang.goodstrade.hook.Metrics;
import cc.sbsj.polang.goodstrade.hook.Papi;
import cc.sbsj.polang.goodstrade.task.RunTask;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class GoodsTrade extends JavaPlugin {
    public static final String PREFIX_KEY = "prefix";
    public static GoodsTrade instance;
    public Metrics metrics;
    public static Config config;
    public static Lang lang;
    
    public static String getPrefix() {
        return lang.getString(PREFIX_KEY);
    }
    public static PlayerDataManager playerDataManager;
    public static boolean pluginEnabled;
    @Override
    public void onEnable() {
        instance = this;
        if (checkDependencies())
        {
            pluginEnabled = true;
        } else {
            pluginEnabled = false;
            getLogger().severe("§c为了阻止可能出现的错误，插件停止加载功能");
            return;
        }
        config = new Config(this);
        lang = new Lang(this);
        ViewConfig.load(this);
        playerDataManager = new PlayerDataManager(this);

        getLogger().info(getPrefix() + "§3插件版本: §bv" + this.getDescription().getVersion());
        getLogger().info(getPrefix() + "§3插件功能: §e" + this.getDescription().getDescription());

        getCommand("goodstrade").setExecutor(new GoodsTradeCommand(this));
        getLogger().info("§2命令成功加载");
        getServer().getPluginManager().registerEvents(new Events(), this);
        registerPaperSecurityEvents();
        getLogger().info("§2事件监听器成功注册");
        //每十分钟运行一次检查
        this.getServer().getScheduler().runTaskTimer(this, new RunTask(), 20L, 12000L);

        getLogger().info(getPrefix() + "§a成功加载了喵~");
    }

    private void registerPaperSecurityEvents() {
        try {
            Class.forName(
                    "com.destroystokyo.paper.event.server.AsyncTabCompleteEvent",
                    false,
                    getClass().getClassLoader()
            );
            getServer().getPluginManager().registerEvents(new PaperSecurityEvents(), this);
            getLogger().info("Paper安全检测已启用");
        } catch (ClassNotFoundException e) {
            getLogger().info("Paper安全检测未启用");
        } catch (LinkageError ignored) {
            getLogger().warning("Paper安全检测不可用，已跳过");
        }
    }

    private boolean checkDependencies() {

        try {
            Material test = XMaterial.STONE.get();
            if (test == null) {
                getLogger().warning("§cXSeries 解析物品失败！请确保 XSeries 已正确打包进插件 jar。");
                return false;
            } else {
                getLogger().info("§2依赖库 XSeries 加载正常");
            }
        } catch (NoClassDefFoundError e) {
            getLogger().severe("§c致命错误：找不到 XSeries 类！插件将无法正常工作。");
            getLogger().severe("§c请检查 build.gradle 中是否正确配置了 shadowJar 任务来重定位并打包 XSeries。");
            return false;
        } catch (Exception e) {
            getLogger().warning("§cXSeries 初始化异常: " + e.getMessage());
            return false;
        }

        // Hook PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Papi(this).register();
            getLogger().info("§2PlaceholderAPI 变量已注册");
        } else {
            getLogger().info("§7未检测到 PlaceholderAPI，有关变量功能将无法使用");
        }

        metrics = new Metrics(this, 30110);
        return true;
    }

    @Override
    public void onDisable() {
        if (pluginEnabled)
        {
            TradeManager.stopAllTrades();
            // 保存玩家数据到文件
            if (playerDataManager != null) {
                playerDataManager.save();
            }
            metrics.shutdown();
            getLogger().info("§a插件卸载了喵~");
        }
    }
}
