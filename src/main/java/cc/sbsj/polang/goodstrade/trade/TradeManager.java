package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.gui.view.TradeView;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.w3c.dom.Text;

import java.util.*;

public class TradeManager {
    private static final Map<UUID, TradeSession> sessions = new HashMap<>();
    public static final Map<UUID, Set<UUID>> accepts = new HashMap<>();


    //创建交易
    public static TradeSession createSession(Player sender, Player target, TradeView view) {
        TradeSession session = new TradeSession(sender, target, view);

        sessions.put(sender.getUniqueId(), session);
        sessions.put(target.getUniqueId(), session);
        return session;
    }

    //获取交易状态
    public static TradeSession getSession(Player player) {
        return sessions.get(player.getUniqueId());
    }

    //删除双方交易状态
    public static void removeSession(Player player) {
        if (!isTrade(player)) return;
        Player otherPlayer = getOtherPlayer(player, sessions.get(player.getUniqueId()));
        sessions.remove(otherPlayer.getUniqueId());
        //移除发起关闭的人
        sessions.remove(player.getUniqueId());
    }

    //获取另一个在交易的玩家
    public static Player getOtherPlayer(Player player, TradeSession session) {
        if (session.getSenderPlayer().equals(player)) {
            // 返回被发起人
            return session.getTargetPlayer();
        } else {
            //返回发起人
            return session.getSenderPlayer();
        }
    }

    //判断玩家是否在交易中
    public static boolean isTrade(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    //完整的取消交易
    public static void cancelTrade(Player player) {
        TradeSession session = TradeManager.getSession(player);
//        if (!session.bothReady()) {
        Player sender = session.getSenderPlayer();
        session.getView().backPlayerItems(sender);

        Player target = session.getTargetPlayer();
        session.getView().backPlayerItems(target);


        if (session.isPlayerSender(player)) {
            //结束交易
            player.sendMessage(GoodsTrade.PREFIX + "你取消了交易！");
            target.sendMessage(GoodsTrade.PREFIX + "对方取消了交易！");
            TradeManager.removeSession(player);
            target.closeInventory();
        } else {
            //结束交易
            player.sendMessage(GoodsTrade.PREFIX + "你取消了交易！");
            sender.sendMessage(GoodsTrade.PREFIX + "对方取消了交易！");
            TradeManager.removeSession(player);
            sender.closeInventory();
        }

//        }
    }

    //已经结束的取消交易
    //只需要关闭被交易者
    public static void cancelTrade(TradeSession session) {
        Player target = session.getTargetPlayer();
        TradeManager.removeSession(target);
        //必须先移除交易会话再关闭界面
        target.closeInventory();

    }

    //给玩家发物品到背包，如果包满了就丢地上
    public static void addItems(Player player, ItemStack... items) {
        if (items.length == 0) return;

        Map<Integer, ItemStack> excessItems = player.getInventory().addItem(items);
        //没有需要返还的物品提前跳过
        if (excessItems == null || excessItems.isEmpty()) return;
        for (Map.Entry<Integer, ItemStack> entry : excessItems.entrySet()) {
            player.getWorld().dropItem(player.getLocation(), entry.getValue());
        }
        //TODO
        player.sendMessage(GoodsTrade.PREFIX + "§c你的背包已满！多余物品已丢出");
    }

    public static void startTrade(Player senderPlayer, Player targetPlayer) {
        senderPlayer.sendMessage(GoodsTrade.PREFIX + "§2对方接受交易，正在打开界面");
        targetPlayer.sendMessage(GoodsTrade.PREFIX + "§2您已接受交易，正在打开界面");
        accepts.clear();
        TradeView gui = new TradeView();
        gui.open(senderPlayer, targetPlayer);
    }

    public static void interactTradeRequest(Player senderPlayer, Player targetPlayer) {
        Set<UUID> set = accepts.get(senderPlayer.getUniqueId()) == null ? new HashSet<>() : accepts.get(senderPlayer.getUniqueId());
        set.add(senderPlayer.getUniqueId());

        accepts.put(targetPlayer.getUniqueId(), set);
        BaseComponent component = new TextComponent(GoodsTrade.PREFIX + "§7玩家 §b" + senderPlayer.getName() + "§7向你发起交易 §a[点击接受]");
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goodstrade accept " + senderPlayer.getName()));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("点击接受")));
        targetPlayer.sendMessage(component);
    }
}
