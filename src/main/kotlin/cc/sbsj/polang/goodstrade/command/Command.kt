package cc.sbsj.polang.goodstrade.command

import cc.sbsj.polang.goodstrade.GoodsTrade
import cc.sbsj.polang.goodstrade.action.ActionType
import cc.sbsj.polang.goodstrade.colonel.argument.command.PlayerArgument
import cc.sbsj.polang.goodstrade.trade.TradeManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.entity.Player
import pers.neige.colonel.CommandProcessor
import pers.neige.colonel.node.impl.LiteralNode
import pers.neige.colonel.node.impl.RootNode
import pers.neige.neigeitems.annotation.Awake
import pers.neige.neigeitems.utils.CommandUtils
import pers.neige.neigeitems.utils.SchedulerUtils.async

@Suppress("UNUSED")
object Command {
    private val COMMAND_NAME = GoodsTrade::class.java.simpleName.lowercase()
    private val ALIASES = arrayListOf(
        StringBuilder().also {
            for (c in GoodsTrade::class.java.simpleName) {
                if (c.isUpperCase() || c.isDigit()) it.append(c.lowercase())
            }
        }.toString()
    )
    private var command: PluginCommand? = null
    private val node = RootNode<CommandSender, Unit>(COMMAND_NAME).thenTree(
        LiteralNode.literal<CommandSender, Unit>("reload", "r")
            .setNullExecutor { context ->
                async(GoodsTrade.getInstance()) {
                    GoodsTrade.getInstance().scanner.runCustomTask("reload")
                    ActionType.RELOAD.eval(context.source)
                }
            }
    ).thenTree(
        LiteralNode.literal<CommandSender, Unit>("sendTrade", "st")
            .setNullExecutor { context ->
                context.source?.sendMessage("§a用法：/gt sendtrade [接收人]")
                context.source?.sendMessage("§e发起人为可选参数,若不存在默认以输入命令者")
            }
            .thenArgument("target", PlayerArgument.NONNULL)
            .setNullExecutor { context ->
                val sender = context.source
                if (sender !is Player) {
                    ActionType.INVALID_SENDER.eval(sender)
                    return@setNullExecutor
                }
                val target = context.getArgument<Player>("target")
                if (sender == target) {
                    ActionType.TRADE_SELF.eval(sender)
                    return@setNullExecutor
                }
                TradeManager.sendTradeRequest(sender, target)
            }
            .thenArgument("sender", PlayerArgument.NONNULL)
            .setNullExecutor { context ->
                if (!context.source!!.hasPermission("goodstrade.admin")) return@setNullExecutor
                val target = context.getArgument<Player>("target")
                val sender = context.getArgument<Player>("sender")
                if (sender == target) {
                    ActionType.TRADE_SAME.eval(sender)
                    return@setNullExecutor
                }
                TradeManager.sendTradeRequest(sender, target)
            }
    ).thenTree(
        LiteralNode.literal<CommandSender, Unit>("trade", "t")
            .thenArgument("target", PlayerArgument.NONNULL)
            .thenArgument("sender", PlayerArgument.NONNULL)
            .setNullExecutor { context ->
                val target = context.getArgument<Player>("target")
                val sender = context.getArgument<Player>("sender")
                if (sender == target) {
                    ActionType.TRADE_SAME.eval(sender)
                    return@setNullExecutor
                }
                TradeManager.startTrade(sender, target)
            }
    ).thenTree(
        LiteralNode.literal<CommandSender, Unit>("accept", "a")
            .setNullExecutor { context ->
                val sender = context.source
                if (sender !is Player) {
                    ActionType.INVALID_SENDER.eval(sender)
                    return@setNullExecutor
                }
                val requests = TradeManager.pendingRequests[sender.uniqueId]
                if (requests == null || requests.isEmpty()) {
                    ActionType.NO_REQUEST.eval(sender)
                    return@setNullExecutor
                }

                val request = requests.first()
                val target = Bukkit.getPlayer(request.senderId)
                if (target == null) {
                    ActionType.REQUEST_PLAYER_OFFLINE.eval(sender)
                    return@setNullExecutor
                }
                TradeManager.startTrade(target, sender)
            }
            .thenArgument("target", PlayerArgument.NONNULL)
            .setNullExecutor { context ->
                val sender = context.source
                if (sender !is Player) {
                    ActionType.INVALID_SENDER.eval(sender)
                    return@setNullExecutor
                }
                val target = context.getArgument<Player>("target")
                if (sender == target) {
                    ActionType.TRADE_SAME.eval(sender)
                    return@setNullExecutor
                }
                val requests = TradeManager.pendingRequests[sender.uniqueId]
                if (requests == null || requests.isEmpty()) {
                    ActionType.NO_REQUEST.eval(sender)
                    return@setNullExecutor
                }
                if (requests.none { it.senderId == target.uniqueId }) {
                    ActionType.NO_SUCH_REQUEST.eval(
                        sender, hashMapOf<String, Any?>(
                            "name" to target.name
                        )
                    )
                    return@setNullExecutor
                }
                TradeManager.startTrade(target, sender)
            }
    )

    fun CommandSender.help() {
        if (!hasPermission("goodstrade.admin")) return
        val firstAlias = ALIASES.first()
        sendMessage("§b方括号包裹的是指令参数, 圆括号包裹的是指令缩写")
        sendMessage("§b例如 reload(r) 可以写作 reload 也可以写作 r")
        sendMessage("§b所有可用指令如下:")
        sendMessage("  /${firstAlias} reload(r) §e重载")

        sendMessage("  /${firstAlias} sendTrade(st) [接收玩家名] (发起玩家名) §e向指定玩家发送交易请求, 填写第二个玩家名参数时则为强制玩家发起交易请求")
        sendMessage("  /${firstAlias} trade(t) [接收玩家名] [发起玩家名] §e强制两玩家发起交易")
        sendMessage("  /${firstAlias} accept(a) (玩家名) §e接受消息请求")
    }

    @Awake(lifeCycle = Awake.LifeCycle.ENABLE)
    fun init() {
        command = CommandUtils.newPluginCommand(COMMAND_NAME, GoodsTrade.getInstance())?.apply {
            aliases = ALIASES
            permission = "$COMMAND_NAME.command"
            CommandProcessor.processCommand(this, node) { context ->
                context.source?.help()
            }
            CommandUtils.getCommandMap().register(this.name, this)
        }
    }
}
