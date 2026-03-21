package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.action.ActionType;
import cc.sbsj.polang.goodstrade.gui.view.TradeView;
import cc.sbsj.polang.goodstrade.manager.ConfigManager;
import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import pers.neige.neigeitems.annotation.Awake;
import pers.neige.neigeitems.annotation.CustomTask;
import pers.neige.neigeitems.annotation.Schedule;

import java.util.*;

import static org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLUGIN;

public class TradeManager {
    public static final Map<UUID, List<TradeRequest>> pendingRequests = new HashMap<>();
    private static final Map<UUID, TradeSession> sessions = new HashMap<>();
    private static final long DEFAULT_COOLDOWN = 30000; // 默认冷却 30 秒


    //创建交易
    public static TradeSession createSession(Player sender, Player target, TradeView view) {
        val session = new TradeSession(sender, target, view);

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
        val otherPlayer = getOtherPlayer(player, sessions.get(player.getUniqueId()));
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
        val session = TradeManager.getSession(player);

        val sender = session.getSenderPlayer();
        session.getView().backPlayerItems(sender);

        val target = session.getTargetPlayer();
        session.getView().backPlayerItems(target);

        if (session.isPlayerSender(player)) {
            //发起者结束交易
            ActionType.YOU_CANCEL_TRADE.eval(player);
            ActionType.TARGET_CANCEL_TRADE.eval(target);
            TradeManager.removeSession(player);
            target.closeInventory();
        } else {
            //被发起者结束交易
            ActionType.YOU_CANCEL_TRADE.eval(player);
            ActionType.TARGET_CANCEL_TRADE.eval(sender);
            TradeManager.removeSession(player);
            sender.closeInventory();
        }
    }

    //已经结束的取消交易
    //只需要关闭被交易者
    public static void cancelTrade(TradeSession session) {
        val target = session.getTargetPlayer();
        TradeManager.removeSession(target);
        //必须先移除交易会话再关闭界面
        target.closeInventory();
    }

    public static void startTrade(Player senderPlayer, Player targetPlayer) {
        if (!pendingRequests.containsKey(targetPlayer.getUniqueId())) {
            ActionType.NO_REQUEST.eval(targetPlayer);
            return;
        }
        ActionType.OPENING_TRADE_GUI.eval(senderPlayer);
        ActionType.OPENING_TRADE_GUI.eval(targetPlayer);
        //打开界面后移除他俩的交易请求
        pendingRequests.remove(senderPlayer.getUniqueId());
        pendingRequests.remove(targetPlayer.getUniqueId());
        val gui = new TradeView();
        gui.open(senderPlayer, targetPlayer);
    }

    public static void sendTradeRequest(Player senderPlayer, Player targetPlayer) {
        if (isInCooldown(senderPlayer, targetPlayer)) {
//            long remainingSeconds = getRemainingCooldownSeconds(senderPlayer, targetPlayer);
            ActionType.REQUEST_COOLDOWN.eval(senderPlayer);
            return;
        }

        addRequest(senderPlayer, targetPlayer);

        val component = new TextComponent(ConfigManager.requestText.replace("<name>", senderPlayer.getName()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goodstrade accept " + senderPlayer.getName()));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("点击可确认")));
        targetPlayer.spigot().sendMessage(component);
        val params = new HashMap<String, Object>();
        params.put("name", targetPlayer.getName());
        ActionType.REQUEST_SENT.eval(senderPlayer, params);
    }

    private static void addRequest(Player sender, Player target) {
        val targetId = target.getUniqueId();
        val request = new TradeRequest(sender, target, DEFAULT_COOLDOWN);

        pendingRequests.computeIfAbsent(targetId, k -> new ArrayList<>()).add(request);
    }

    private static boolean isInCooldown(Player sender, Player target) {
        val targetId = target.getUniqueId();
        val requests = pendingRequests.get(targetId);

        if (requests == null || requests.isEmpty()) {
            return false;
        }

        for (val request : requests) {
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

    @Schedule(period = 12000L)
    public static void cleanupExpiredRequests() {
        val iterator = pendingRequests.entrySet().iterator();

        while (iterator.hasNext()) {
            val entry = iterator.next();
            val requests = entry.getValue();

            requests.removeIf(TradeRequest::isExpired);

            if (requests.isEmpty()) {
                iterator.remove();
            }
        }
    }

    /**
     * 关闭所有正在交易的玩家界面并返还物品（用于 reload 或服务器关闭）
     */
    @CustomTask(taskId = "reload", priority = EventPriority.LOW)
    @Awake(lifeCycle = Awake.LifeCycle.DISABLE)
    public static void stopAllTrades() {
        pendingRequests.clear();
        if (sessions.isEmpty()) return;

        // 复制一份避免并发修改异常
//        List<TradeSession> sessionList = new ArrayList<>(sessions.values());

        for (val session : sessions.values()) {
            try {
                val sender = session.getSenderPlayer();
                val target = session.getTargetPlayer();
                // 检查玩家是否在线
                if (sender != null && target != null) {
                    if (session.getView().runnable != null) session.getView().runnable.cancel();
                    sender.closeInventory(PLUGIN);
                    session.getView().backPlayerItems(sender); // 返还物品
                    ActionType.CANCEL_BY_RELOAD.eval(sender);
                    target.closeInventory(PLUGIN);
                    session.getView().backPlayerItems(target); // 返还物品
                    ActionType.CANCEL_BY_RELOAD.eval(target);

                    TradeManager.removeSession(sender);
                }
            } catch (Exception e) {
                GoodsTrade.getInstance().getLogger().warning("关闭交易时发生错误：" + e.getMessage());
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

        val iterator = pendingRequests.entrySet().iterator();
        while (iterator.hasNext()) {
            val entry = iterator.next();
            val requests = entry.getValue();
            requests.removeIf(req -> req.isSameSender(player));

            if (requests.isEmpty()) {
                iterator.remove();
            }
        }
    }
}
