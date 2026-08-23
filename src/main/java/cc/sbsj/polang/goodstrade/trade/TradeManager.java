package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.compat.ServerCompatibility;
import cc.sbsj.polang.goodstrade.gui.view.TradeView;
import cc.sbsj.polang.goodstrade.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


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

    public static TradeSession createTestSession(Player administrator, String virtualPlayerName, TradeView view) {
        TradeSession session = TradeSession.createTest(administrator, virtualPlayerName, view);
        sessions.put(administrator.getUniqueId(), session);
        return session;
    }

    //获取交易状态
    public static TradeSession getSession(Player player) {
        return sessions.get(player.getUniqueId());
    }

    //删除双方交易状态
    public static void removeSession(Player player) {
        if (!isTrade(player)) return;
        TradeSession session = sessions.get(player.getUniqueId());
        if (session.isTestMode()) {
            sessions.remove(player.getUniqueId());
            return;
        }
        Player otherPlayer = getOtherPlayer(player, session);
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
        if (session == null) return;

        if (session.isTestMode()) {
            if (session.getView().runnable != null) {
                try {
                    session.getView().runnable.cancel();
                } catch (IllegalStateException ignored) {
                    // The countdown was not scheduled or has already stopped.
                }
            }
            session.getView().backPlayerItems(player);
            player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.test-cancelled"));
            TradeManager.removeSession(player);
            return;
        }

        Player sender = session.getSenderPlayer();
        session.getView().backPlayerItems(sender);

        Player target = session.getTargetPlayer();
        session.getView().backPlayerItems(target);

        if (session.isPlayerSender(player)) {
            //发起者结束交易
            player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.cancelled-by-self"));
            target.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.cancelled-by-other"));
            TradeManager.removeSession(player);
            // 手动处理光标物品后清空，防止 Bukkit closeInventory 内部重复返还
            returnCursorItem(target);
            // 统一走兼容层：1.12 没有 closeInventory(Reason)。
            ServerCompatibility.closeInventory(target);
        } else {
            //被发起者结束交易
            player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.cancelled-by-self"));
            sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.cancelled-by-other"));
            TradeManager.removeSession(player);
            returnCursorItem(sender);
            // 统一走兼容层：1.12 没有 closeInventory(Reason)。
            ServerCompatibility.closeInventory(sender);
        }
    }

    //已经结束的取消交易
    //只需要关闭被交易者
    public static void cancelTrade(TradeSession session) {
        if (session.isTestMode()) {
            Player administrator = session.getSenderPlayer();
            session.getView().backPlayerItems(administrator);
            TradeManager.removeSession(administrator);
            returnCursorItem(administrator);
            ServerCompatibility.closeInventory(administrator);
            return;
        }
        Player target = session.getTargetPlayer();
        TradeManager.removeSession(target);
        //必须先移除交易会话再关闭界面
        returnCursorItem(target);
        // 统一走兼容层：1.12 没有 closeInventory(Reason)。
        ServerCompatibility.closeInventory(target);
    }

    /**
     * 手动将光标物品放入玩家背包并清空光标，防止 closeInventory 内部重复返还
     */
    private static void returnCursorItem(Player player) {
        ItemStack cursor = player.getOpenInventory().getCursor();
        if (Utils.isItemStackNotEmpty(cursor)) {
            Utils.addItems(player, cursor);
        }
        player.setItemOnCursor(null);
    }

    public static void startTrade(Player senderPlayer, Player targetPlayer) {
        if (isTrade(senderPlayer) || isTrade(targetPlayer)) {
            senderPlayer.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.already-trading"));
            targetPlayer.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.already-trading"));
            return;
        }
        if (!pendingRequests.containsKey(targetPlayer.getUniqueId())) {
            targetPlayer.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-request.no-pending"));
            return;
        }
        senderPlayer.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.opening"));
        targetPlayer.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.opening"));
        //打开界面后移除他俩的交易请求
        pendingRequests.remove(senderPlayer.getUniqueId());
        pendingRequests.remove(targetPlayer.getUniqueId());
        TradeView gui = new TradeView();
        gui.open(senderPlayer, targetPlayer);
    }

    public static void sendTradeRequest(Player senderPlayer, Player targetPlayer) {
        // 检查目标玩家是否接受交易请求
        if (!GoodsTrade.playerDataManager.isTradeAccept(targetPlayer.getUniqueId())) {
            senderPlayer.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-request.target-closed"));
            return;
        }

        if (isInCooldown(senderPlayer, targetPlayer)) {
//            long remainingSeconds = getRemainingCooldownSeconds(senderPlayer, targetPlayer);
            senderPlayer.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-request.cooldown"));
            return;
        }

        addRequest(senderPlayer, targetPlayer);

        String receivedMsg = GoodsTrade.lang.replacePlaceholders(
                GoodsTrade.lang.getString("trade-request.received"),
                "%player%", senderPlayer.getName()
        );
        // 不支持 BungeeChat 的服务端会由兼容层降级为普通文本消息。
        ServerCompatibility.sendClickableMessage(
                targetPlayer,
                GoodsTrade.getPrefix() + receivedMsg,
                "/goodstrade accept " + senderPlayer.getName(),
                GoodsTrade.lang.getString("trade-request.hover")
        );
        String sentMsg = GoodsTrade.lang.replacePlaceholders(
                GoodsTrade.lang.getString("trade-request.sent"),
                "%target%", targetPlayer.getName()
        );
        senderPlayer.sendMessage(GoodsTrade.getPrefix() + sentMsg);
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

    /**
     * 清理过期的请求
     */
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
        Set<TradeSession> sessionList = new HashSet<>(sessions.values());

        for (TradeSession session : sessionList) {
            try {
                Player sender = session.getSenderPlayer();
                Player target = session.getTargetPlayer();
                if (session.getView().runnable != null) session.getView().runnable.cancel();

                if (session.isTestMode()) {
                    session.getView().backPlayerItems(sender);
                    returnCursorItem(sender);
                    ServerCompatibility.closeInventory(sender);
                    sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.cancelled-by-reload"));
                    continue;
                }

                if (sender != null) {
                    session.getView().backPlayerItems(sender);
                    returnCursorItem(sender);
                    // 统一走兼容层：1.12 没有 closeInventory(Reason)。
                    ServerCompatibility.closeInventory(sender);
                    sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.cancelled-by-reload"));
                }
                if (target != null) {
                    session.getView().backPlayerItems(target);
                    returnCursorItem(target);
                    // 统一走兼容层：1.12 没有 closeInventory(Reason)。
                    ServerCompatibility.closeInventory(target);
                    target.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.cancelled-by-reload"));
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
        return Collections.unmodifiableSet(new HashSet<>(sessions.values()));
    }

    public static int getActiveTradeCount() {
        return new HashSet<>(sessions.values()).size();
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
