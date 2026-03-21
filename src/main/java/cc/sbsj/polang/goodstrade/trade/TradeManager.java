package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.gui.view.TradeView;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.*;

import static org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLUGIN;

public class TradeManager {
    private static final Map<UUID, TradeSession> sessions = new HashMap<>();
    public static final Map<UUID, List<TradeRequest>> pendingRequests = new HashMap<>();
    private static final long DEFAULT_COOLDOWN = 30000; // 默认冷却 30 秒


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
            return session.getTargetPlayerExact();
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

        Player sender = session.getSenderPlayer();
        session.getView().backPlayerItems(sender);

        Player target = session.getTargetPlayerExact();
        session.getView().backPlayerItems(target);

        if (session.isPlayerSender(player)) {
            //发起者结束交易
            player.sendMessage(GoodsTrade.PREFIX + "你取消了交易！");
            target.sendMessage(GoodsTrade.PREFIX + "对方取消了交易！");
            TradeManager.removeSession(player);
            target.closeInventory();
        } else {
            //被发起者结束交易
            player.sendMessage(GoodsTrade.PREFIX + "你取消了交易！");
            sender.sendMessage(GoodsTrade.PREFIX + "对方取消了交易！");
            TradeManager.removeSession(player);
            sender.closeInventory();
        }
    }

    //已经结束的取消交易
    //只需要关闭被交易者
    public static void cancelTrade(TradeSession session) {
        Player target = session.getTargetPlayerExact();
        TradeManager.removeSession(target);
        //必须先移除交易会话再关闭界面
        target.closeInventory();
    }

    public static void startTrade(Player senderPlayer, Player targetPlayerExact) {
        if (!pendingRequests.containsKey(targetPlayerExact.getUniqueId())) {
            targetPlayerExact.sendMessage(GoodsTrade.PREFIX + "§c你当前没有交易请求！");
            return;
        }
        senderPlayer.sendMessage(GoodsTrade.PREFIX + "§2正在为您打开交易界面");
        targetPlayerExact.sendMessage(GoodsTrade.PREFIX + "§2正在为您打开交易界面");
        //打开界面后移除他俩的交易请求
        pendingRequests.remove(senderPlayer.getUniqueId());
        pendingRequests.remove(targetPlayerExact.getUniqueId());
        TradeView gui = new TradeView();
        gui.open(senderPlayer, targetPlayerExact);
    }

    public static void sendTradeRequest(Player senderPlayer, Player targetPlayerExact) {
        if (isInCooldown(senderPlayer, targetPlayerExact)) {
//            long remainingSeconds = getRemainingCooldownSeconds(senderPlayer, targetPlayerExact);
            senderPlayer.sendMessage(GoodsTrade.PREFIX + "§c发起交易过于频繁，请等待一会再重试");
            return;
        }

        addRequest(senderPlayer, targetPlayerExact);

        BaseComponent component = new TextComponent(GoodsTrade.PREFIX + "§7玩家 §e" + senderPlayer.getName() + " §7向你发起交易 §a[点击接受]");
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goodstrade accept " + senderPlayer.getName()));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("点击可确认")));
        targetPlayerExact.sendMessage(component);
        senderPlayer.sendMessage(GoodsTrade.PREFIX + "§7已向玩家 §e" + targetPlayerExact.getName() + " §7发起交易");
    }

    private static void addRequest(Player sender, Player target) {
        UUID targetId = target.getUniqueId();
        TradeRequest request = new TradeRequest(sender, target, DEFAULT_COOLDOWN);

        pendingRequests.computeIfAbsent(targetId, k -> new ArrayList<>()).add(request);
    }

    private static boolean isInCooldown(Player sender, Player target) {
        UUID targetId = target.getUniqueId();
        List<TradeRequest> requests = pendingRequests.get(targetId);

        if (requests == null || requests.isEmpty()) {
            return false;
        }

        for (TradeRequest request : requests) {
            if (request.isSameSender(sender) && !request.isExpired()) {
                return true;
            }
        }

        return false;
    }

//    private static long getRemainingCooldownSeconds(Player sender, Player target) {
//        UUID targetId = target.getUniqueId();
//        List<TradeRequest> requests = pendingRequests.get(targetId);
//
//        if (requests == null || requests.isEmpty()) {
//            return 0;
//        }
//
//        for (TradeRequest request : requests) {
//            if (request.isSameSender(sender) && !request.isExpired()) {
//                return (request.getRemainingCooldown() / 1000) + 1;
//            }
//        }
//
//        return 0;
//    }


//    public static List<TradeRequest> getRequests(Player player) {
//        List<TradeRequest> requests = pendingRequests.get(player.getUniqueId());
//        return requests == null ? Collections.emptyList() : requests;
//    }

    public static void cleanupExpiredRequests() {
        Iterator<Map.Entry<UUID, List<TradeRequest>>> iterator = pendingRequests.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, List<TradeRequest>> entry = iterator.next();
            List<TradeRequest> requests = entry.getValue();

            requests.removeIf(TradeRequest::isExpired);

            if (requests.isEmpty()) {
                iterator.remove();
            }
        }
    }

    /**
     * 关闭所有正在交易的玩家界面并返还物品（用于 reload 或服务器关闭）
     */
    public static void stopAllTrades() {
        if (sessions.isEmpty()) return;

        // 复制一份避免并发修改异常
//        List<TradeSession> sessionList = new ArrayList<>(sessions.values());

        for (TradeSession session : sessions.values()) {
            try {
                Player sender = session.getSenderPlayer();
                Player target = session.getTargetPlayerExact();
                // 检查玩家是否在线
                if (sender != null && target != null) {
                    if (session.getView().runnable != null) session.getView().runnable.cancel();
                    sender.closeInventory(PLUGIN);
                    session.getView().backPlayerItems(sender); // 返还物品
                    sender.sendMessage(GoodsTrade.PREFIX + "§c功能重载中，交易已取消");
                    target.closeInventory(PLUGIN);
                    session.getView().backPlayerItems(target); // 返还物品
                    target.sendMessage(GoodsTrade.PREFIX + "§c功能重载中，交易已取消");

                    TradeManager.removeSession(sender);
                }
            } catch (Exception e) {
                GoodsTrade.instance.getLogger().warning("关闭交易时发生错误：" + e.getMessage());
            }
        }

        // 清空所有会话
        sessions.clear();

        // 清空所有请求
        pendingRequests.clear();
    }

    public static Collection<TradeSession> getAllSessions() {
        return Collections.unmodifiableCollection(sessions.values());
    }

    public static int getActiveTradeCount() {
        return sessions.size() / 2; // 除以 2 因为每对交易有 2 个记录
    }

    public static void cancelAllRequests(Player player) {
        pendingRequests.remove(player.getUniqueId());

        Iterator<Map.Entry<UUID, List<TradeRequest>>> iterator = pendingRequests.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, List<TradeRequest>> entry = iterator.next();
            List<TradeRequest> requests = entry.getValue();
            requests.removeIf(req -> req.isSameSender(player));

            if (requests.isEmpty()) {
                iterator.remove();
            }
        }
    }
}
