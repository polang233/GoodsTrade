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
    public String getPermission() {
        return "goodstrade.command.reload";
    }
    @Override
    public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) return false;
        TradeManager.stopAllTrades();
        GoodsTrade.config.reload();
        // 重载时保存并重新加载玩家数据
        GoodsTrade.playerDataManager.save();
        GoodsTrade.playerDataManager.load();
        TradeManager.pendingRequests.clear();
        sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("reload.success"));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}
