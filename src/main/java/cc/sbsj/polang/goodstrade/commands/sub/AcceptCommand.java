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
    public String getPermission() {
        return "goodstrade.command.accept";
    }
    @Override
    public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("command.player-only"));
            return false;
        }
        TradeManager.cleanupExpiredRequests();
        if (args.length == 0) {
            Player player = (Player) sender;

            List<TradeRequest> requests = TradeManager.pendingRequests.get(player.getUniqueId());
            if (requests == null || requests.isEmpty()) {
                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-request.no-pending"));
                return false;
            }
            int acceptCount = requests.size();
            if (acceptCount == 1) {
                TradeRequest request = requests.get(0);
                Player targetPlayer = Bukkit.getPlayer(request.getSenderId());
                if (targetPlayer == null) {
                    player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("player.offline"));
                    return false;
                }
                return TradeManager.acceptTrade(targetPlayer, player);
            }
            player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-request.select-player"));
            return false;
        }
        if (args.length == 1) {
            Player targetPlayer = Bukkit.getPlayerExact(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("player.offline"));
                return false;
            }
            return TradeManager.acceptTrade(targetPlayer, (Player) sender);
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}