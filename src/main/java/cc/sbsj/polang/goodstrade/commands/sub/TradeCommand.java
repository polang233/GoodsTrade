package cc.sbsj.polang.goodstrade.commands.sub;


import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.commands.SubCommand;
import cc.sbsj.polang.goodstrade.commands.SubCommandAnnotation;
import cc.sbsj.polang.goodstrade.gui.view.TradeView;
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
    public boolean execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0)
        {
            sender.sendMessage(GoodsTrade.PREFIX + "§a用法：/gt trade [接收人] <发起人>");
            sender.sendMessage(GoodsTrade.PREFIX + "§e发起人为可选参数,若不存在默认以输入命令者");
            return false;
        }
        if (args.length == 1)
        {
            if (sender instanceof Player)
            {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null)
                {
                    sender.sendMessage(GoodsTrade.PREFIX+ "§c输入用户不存在或不在线");
                    return false;
                }
                TradeView gui = new TradeView();
                gui.open((Player) sender, player);
                return true;
            } else {
                sender.sendMessage("控制台必须输入发起人与被发起人");
                return false;
            }
        }
        if (args.length == 2)
        {
            TradeView gui = new TradeView();
            //发起人
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null)
            {
                sender.sendMessage(GoodsTrade.PREFIX+ "§c发起人不在线！");
                return false;
            }
            Player player2 = Bukkit.getPlayer(args[1]);
            if (player2 == null)
            {
                sender.sendMessage(GoodsTrade.PREFIX+ "§c被受邀者不在线");
                return false;
            }
            gui.open(player, player2);
            return true;
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
