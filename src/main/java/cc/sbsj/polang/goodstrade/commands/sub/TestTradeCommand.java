package cc.sbsj.polang.goodstrade.commands.sub;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommand;
import cc.sbsj.polang.goodstrade.commands.annotation.SubCommandAnnotation;
import cc.sbsj.polang.goodstrade.gui.view.TradeView;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@SubCommandAnnotation(name = "test")
@SuppressWarnings("unused")
public class TestTradeCommand implements SubCommand {
    private static final String DEFAULT_VIRTUAL_PLAYER = "TestPlayer";

    @Override
    public String getPermission() {
        return "goodstrade.command.test";
    }

    @Override
    public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("command.player-only"));
            return false;
        }
        if (args.length > 1) {
            sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("command.usage.test"));
            return false;
        }

        Player administrator = (Player) sender;
        String virtualPlayerName = args.length == 0 ? DEFAULT_VIRTUAL_PLAYER : args[0];
        if (!virtualPlayerName.matches("[A-Za-z0-9_]{1,16}")) {
            sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("test-trade.invalid-name"));
            return false;
        }
        if (Bukkit.getPlayerExact(virtualPlayerName) != null) {
            sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("test-trade.player-exists"));
            return false;
        }
        if (TradeManager.isTrade(administrator)) {
            sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("test-trade.already-trading"));
            return false;
        }

        new TradeView().openTest(administrator, virtualPlayerName);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}
