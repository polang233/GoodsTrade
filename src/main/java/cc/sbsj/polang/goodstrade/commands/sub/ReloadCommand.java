package cc.sbsj.polang.goodstrade.commands.sub;


import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommand;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommandAnnotation;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@SubCommandAnnotation(name = "reload")
@SuppressWarnings("unused")
public class ReloadCommand implements SubCommand {

    @Override
    public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("goodstrade.admin")) return false;
        GoodsTrade.config.reload();
        TradeManager.pendingRequests.clear();
        TradeManager.stopAllTrades();
        sender.sendMessage(GoodsTrade.PREFIX + "§a配置文件已重载！");

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}
