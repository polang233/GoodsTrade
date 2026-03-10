package cc.sbsj.polang.goodstrade.commands.sub;


import cc.sbsj.polang.goodstrade.commands.main.SubCommand;
import cc.sbsj.polang.goodstrade.commands.main.SubCommandAnnotation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@SubCommandAnnotation(name = "trade")
public class TradeCommand implements SubCommand {

    @Override
    public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}
