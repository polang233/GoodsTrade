package cc.sbsj.polang.goodstrade.commands.sub;


import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommand;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommandAnnotation;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SubCommandAnnotation(name = "accept")
@SuppressWarnings("unused")
public class AcceptCommand implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;
            if (!TradeManager.accepts.containsKey(player.getUniqueId())) return false;
            Set<UUID> uuids = TradeManager.accepts.get(player.getUniqueId());
            int acceptCount = uuids.size();
            if (acceptCount == 1) {
                Player targetPlayer = Bukkit.getPlayer(uuids.iterator().next());
                if (targetPlayer == null) {
                    player.sendMessage(GoodsTrade.PREFIX + "§c该玩家不在线");
                    return false;
                }
                TradeManager.startTrade(targetPlayer, player);
                return true;
            }
            player.sendMessage(GoodsTrade.PREFIX + "§c请选择一个玩家");
            return false;
        }
        if (args.length == 1) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(GoodsTrade.PREFIX + "§c该玩家不在线");
                return false;
            }

            TradeManager.startTrade(targetPlayer, (Player) sender);
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}
