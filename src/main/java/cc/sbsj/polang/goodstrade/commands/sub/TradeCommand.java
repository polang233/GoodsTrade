package cc.sbsj.polang.goodstrade.commands.sub;


import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommand;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommandAnnotation;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SubCommandAnnotation(name = "trade")
@SuppressWarnings("unused")
public class TradeCommand implements SubCommand {
    @Override
    public String getPermission() {
        return "goodstrade.command.trade";
    }
    @Override
    public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) return false;
        if (args.length == 0) {
            sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("command.usage.trade"));
            return false;
        }
        if (args.length == 1) {
            sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("console.need-both-players"));
            return false;
        }
        if (args.length == 2) {
            if (!sender.hasPermission(getPermission())) return false;
            //发起人
            Player player = Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("player.offline"));
                return false;
            }
            Player player2 = Bukkit.getPlayerExact(args[1]);
            if (player2 == null) {
                sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("player.offline"));
                return false;
            }
            if (player == player2) {
                sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("player.different-players"));
                return false;
            }
            return TradeManager.startTrade(player, player2);
        }

        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
        if (args.length == 1) {
            return list;
        }
        if (args.length == 2) {
            return list;
        }
        return Collections.emptyList();
    }
}
