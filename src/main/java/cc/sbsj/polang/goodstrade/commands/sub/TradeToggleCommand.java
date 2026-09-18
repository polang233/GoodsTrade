package cc.sbsj.polang.goodstrade.commands.sub;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommand;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommandAnnotation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 交易状态切换命令
 * 用法：/gt toggle [on|off]
 * 无参数时切换当前状态，on 开启接受交易，off 关闭接受交易
 */
@SubCommandAnnotation(name = "toggle")
@SuppressWarnings("unused")
public class TradeToggleCommand implements SubCommand {

    @Override
    public String getPermission() {
        return "goodstrade.command.toggle";
    }

    @Override
    public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("command.player-only"));
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // 切换模式
            boolean newState = GoodsTrade.playerDataManager.toggleTradeAccept(player.getUniqueId());
            sendToggleMessage(player, newState);
            return true;
        }

        if (args.length == 1) {
            String param = args[0].toLowerCase();
            if ( param.equals("true")) {
                GoodsTrade.playerDataManager.setTradeAccept(player.getUniqueId(), true);
                sendToggleMessage(player, true);
                return true;
            } else if (param.equals("false")) {
                GoodsTrade.playerDataManager.setTradeAccept(player.getUniqueId(), false);
                sendToggleMessage(player, false);
                return true;
            } else {
                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.labeled("command.usage.toggle", label));
                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("command.usage.toggle-hint"));
                return false;
            }
        }

        player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.labeled("command.usage.toggle", label));
        return false;
    }

    private void sendToggleMessage(Player player, boolean state) {
        if (state) {
            player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-toggle.enabled"));
        } else {
            player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-toggle.disabled"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("true", "false");
        }
        return Collections.emptyList();
    }
}
