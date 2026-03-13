package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.gui.view.TradeView;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeManager {
    private static final Map<UUID, TradeSession> sessions = new HashMap<>();

    public static TradeSession createSession(Player sender, Player target, TradeView view) {
        TradeSession session = new TradeSession(sender, target, view);

        sessions.put(sender.getUniqueId(), session);
        sessions.put(target.getUniqueId(), session);
        return session;
    }

    public static TradeSession getSession(Player player) {
        return sessions.get(player.getUniqueId());
    }

    public static void removeSession(Player player) {
        if (!isTrade(player)) return;
        Player otherPlayer = getOtherPlayer(player, sessions.get(player.getUniqueId()));
        sessions.remove(otherPlayer.getUniqueId());
        //移除发起关闭的人
        sessions.remove(player.getUniqueId());
    }

    public static Player getOtherPlayer(Player player, TradeSession session) {
        if (session.getSenderPlayer().equals(player)) {
            // 返回被发起人
            return session.getTargetPlayer();
        } else {
            //返回发起人
            return session.getSenderPlayer();
        }
    }

    public static boolean isTrade(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }


    public static void cancelTrade(Player player) {
        TradeSession session = TradeManager.getSession(player);
        if (!session.bothReady()) {
            Player sender = session.getSenderPlayer();
            session.getView().backPlayerItems(sender);


            Player target = session.getTargetPlayer();
            session.getView().backPlayerItems(target);


            if (session.isPlayerSender(player)) {
                //结束交易
                TradeManager.removeSession(player);
                target.closeInventory();
            } else {
                //结束交易
                TradeManager.removeSession(player);
                sender.closeInventory();
            }

        }
    }

    public static void cancelTrade(TradeSession session) {
        Player target = session.getTargetPlayer();
        target.closeInventory();
        TradeManager.removeSession(target);

    }

    public static void addItems(Player player, ItemStack... items) {
        if (items.length == 0) return;

        Map<Integer, ItemStack> excessItems = player.getInventory().addItem(items);
        //没有需要返还的物品提前跳过
        if (excessItems == null || excessItems.isEmpty()) return;
        for (Map.Entry<Integer, ItemStack> entry : excessItems.entrySet()) {
            player.getWorld().dropItem(player.getLocation(), entry.getValue());
        }
        //TODO
        player.sendMessage(GoodsTrade.PREFIX + "你的背包已满！多余物品已丢出");
    }
}
