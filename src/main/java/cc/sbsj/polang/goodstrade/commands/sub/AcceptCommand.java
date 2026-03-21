package cc.sbsj.polang.goodstrade.commands.sub;


import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommand;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommandAnnotation;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import cc.sbsj.polang.goodstrade.trade.TradeRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@SubCommandAnnotation(name = "accept")
@SuppressWarnings("unused")
public class AcceptCommand implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;

            List<TradeRequest> requests = TradeManager.pendingRequests.get(player.getUniqueId());
            if (requests.isEmpty()) return false;

            int acceptCount = requests.size();
            if (acceptCount == 1) {
                TradeRequest request = requests.get(0);
                Player targetPlayerExact = Bukkit.getPlayerExact(request.getSenderId());
                if (targetPlayerExact == null) {
                    player.sendMessage(GoodsTrade.PREFIX + "§c该玩家不在线");
                    return false;
                }
                TradeManager.startTrade(targetPlayerExact, player);
                return true;
            }
            player.sendMessage(GoodsTrade.PREFIX + "§c请选择一个玩家");
            return false;
        }
        if (args.length == 1) {
            Player targetPlayerExact = Bukkit.getPlayerExact(args[0]);
            if (targetPlayerExact == null) {
                sender.sendMessage(GoodsTrade.PREFIX + "§c该玩家不在线");
                return false;
            }
            TradeManager.startTrade(targetPlayerExact, (Player) sender);
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}