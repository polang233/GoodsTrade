package cc.sbsj.polang.goodstrade.manager

import cc.sbsj.polang.goodstrade.GoodsTrade
import cc.sbsj.polang.goodstrade.gui.view.View
import cc.sbsj.polang.goodstrade.trade.TradeManager
import cc.sbsj.polang.goodstrade.trade.TradeSession
import org.bstats.bukkit.Metrics
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.neosearch.stringsearcher.StringSearcher
import pers.neige.neigeitems.action.Action
import pers.neige.neigeitems.annotation.Awake
import pers.neige.neigeitems.annotation.CustomTask
import pers.neige.neigeitems.utils.ConfigUtils.loadConfig

object ConfigManager {
    val config: FileConfiguration get() = GoodsTrade.getInstance().config

    @JvmStatic
    var actions: MutableMap<String, Action> = mutableMapOf()

    @JvmStatic
    var waitTime: Int = 5

    @JvmStatic
    var shiftClickTrade: Boolean = true

    @JvmStatic
    var preventDamageWhenTrade: Boolean = false

    @JvmStatic
    var preventMoveWhenTrade: Boolean = false

    @JvmStatic
    var enableItemBlacklist: Boolean = false

    @JvmStatic
    lateinit var itemNameBlacklist: StringSearcher<String>

    @JvmStatic
    lateinit var itemLoreBlacklist: StringSearcher<String>

    @JvmStatic
    lateinit var requestText: String

    /**
     * 重载配置管理器
     */
    @Awake(lifeCycle = Awake.LifeCycle.ENABLE, priority = EventPriority.LOWEST)
    @CustomTask(taskId = "reload", priority = EventPriority.LOWEST)
    @JvmStatic
    fun reload() {
        saveResource()
        GoodsTrade.getInstance().loadConfig(true)
        initConfig()
    }

    /**
     * 加载默认配置文件
     */
    @JvmStatic
    private fun saveResource() {
        Metrics(GoodsTrade.getInstance(), 30110)
    }

    private fun initConfig() {
        actions = mutableMapOf<String, Action>().also {
            val actionsConfig = config.getConfigurationSection("actions")
            for (key in actionsConfig.getKeys(false)) {
                it[key] = ActionManager.INSTANCE.compile(actionsConfig.get(key))
            }
        }

        waitTime = config.getInt("Trade.Wait-Time").coerceAtMost(64)
        shiftClickTrade = config.getBoolean("Trade.Triggers.Shift-Right-Click", true)
        preventDamageWhenTrade = config.getBoolean("Trade.Safe.Damage", false)
        preventMoveWhenTrade = config.getBoolean("Trade.Safe.Move", false)
        enableItemBlacklist = config.getBoolean("Trade.Item-BlackList.Enable", false)

        val nameBlackListTexts = config.getStringList("Trade.Item-BlackList.Name")
        val loreBlackListTexts = config.getStringList("Trade.Item-BlackList.Lore")
        if (nameBlackListTexts.isEmpty() && loreBlackListTexts.isEmpty()) {
            enableItemBlacklist = false
        }

        itemNameBlacklist = StringSearcher.builder()
            .ignoreOverlaps()
            .addSearchStrings(nameBlackListTexts)
            .build()
        itemLoreBlacklist = StringSearcher.builder()
            .ignoreOverlaps()
            .addSearchStrings(loreBlackListTexts)
            .build()

        requestText = config.getString("texts.request")
    }

    @JvmStatic
    fun hasBlacklistItem(player: Player): Boolean {
        if (!enableItemBlacklist) return false

        val session = TradeManager.getSession(player)
        return if (session.senderPlayer === player) {
            hasBlacklistItem(View.senderTradeSlots, session)
        } else {
            hasBlacklistItem(View.targetTradeSlots, session)
        }
    }

    @JvmStatic
    private fun hasBlacklistItem(
        slots: MutableList<Int>,
        session: TradeSession
    ): Boolean {
        for (slot in slots) {
            val itemStack = session.view.gui.inventory.getItem(slot)
            if (itemStack == null || itemStack.type == Material.AIR) continue
            val itemMeta = itemStack.itemMeta
            if (itemMeta == null) continue
            val displayName = itemMeta.displayName
            if (displayName != null && itemNameBlacklist.containsMatch(displayName)) return true
            val lore = itemMeta.lore
            if (lore != null && lore.stream().anyMatch { itemLoreBlacklist.containsMatch(it) }) return true
        }
        return false
    }
}
