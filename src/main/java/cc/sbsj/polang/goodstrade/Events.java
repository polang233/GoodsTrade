package cc.sbsj.polang.goodstrade;

import cc.sbsj.polang.goodstrade.gui.Gui;
import cc.sbsj.polang.goodstrade.gui.view.View;
import cc.sbsj.polang.goodstrade.compat.ServerCompatibility;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import cc.sbsj.polang.goodstrade.trade.TradeSession;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Events implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null ||
                event.getClick() == ClickType.UNKNOWN ||
                event.getClick() == ClickType.WINDOW_BORDER_RIGHT ||
                event.getClick() == ClickType.WINDOW_BORDER_LEFT ||
                event.getAction() == InventoryAction.UNKNOWN) {
            return; // 忽略无效点击
        }
        Player player = (Player) event.getWhoClicked();

        // 判断是不是这个插件的gui
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof Gui)) return;

        TradeSession session = TradeManager.getSession(player);
        if (session == null) return;
        if (session.bothReady() && !View.readySlots.contains(event.getRawSlot())) {
            player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.confirm-in-progress"));
            event.setCancelled(true);
            return;
        }
        //debug
//        player.sendMessage("§7点击类型: §b" + event.getClick().toString());
//        player.sendMessage("§7点击操作: §3" + event.getAction().toString());
//        player.sendMessage("§7点击格子: §2" + event.getSlot());
//        player.sendMessage("§7原始格子: §a" + event.getRawSlot());
//        String itemType = event.getCurrentItem() == null ? "null" : event.getCurrentItem().getType().toString();
//        String itemType2 = event.getCursor() == null ? "null" : event.getCursor().getType().toString();
//        player.sendMessage("§7手里物品: §e" + itemType2);
//        player.sendMessage("§7点击物品: §6" + itemType);

        //安全处理
        switch (event.getClick()) {
            //拦截shift快捷放入
            case SHIFT_RIGHT:
            case SHIFT_LEFT:
                //拦截双击吸走容器物品操作
            case DOUBLE_CLICK:
                event.setCancelled(true);
                return;
        }
        // 点击的是玩家自己背包
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            //留着，总有用
        } else {
            event.setCancelled(true);
            Gui gui = (Gui) event.getInventory().getHolder();
            int slot = event.getRawSlot();
            if (gui.buttons[slot] != null) {
                gui.buttons[slot].onClick(event);
            } else {
                event.setCancelled(false);
            }
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof Gui)) return;
        Player player = (Player) event.getWhoClicked();
        TradeSession session = TradeManager.getSession(player);
        if (session == null) return;
        boolean sender = session.isPlayerSender(player);
        int topSize = event.getView().getTopInventory().getSize();
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot >= topSize) continue;
            boolean senderSlot = View.isSenderTradeSlot(rawSlot);
            boolean targetSlot = View.isTargetTradeSlot(rawSlot);
            boolean ownTradeSlot = session.isTestMode()
                    ? senderSlot || targetSlot
                    : sender ? senderSlot : targetSlot;
            // Only the player's own offer slots accept dragged items. This also protects
            // divider, ready, and economy buttons from drag-based replacement.
            if (!ownTradeSlot) {
                event.setCancelled(true);
                return;
            }
            boolean locked = senderSlot ? session.isSenderReady() : session.isTargetReady();
            if (locked) {
                event.setCancelled(true);
                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.items-locked"));
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof Gui)) return;
        Player player = (Player) event.getPlayer();

        // 交易已完成或已取消，物品已由调用方处理，直接返回
        // 兼容层标记的插件主动关闭不能再次触发交易取消。
        if (ServerCompatibility.isPluginInventoryClose(event)) {
            return;
        }

        // 该玩家是否正在交易
        if (!TradeManager.isTrade(player)) return;
        TradeSession session = TradeManager.getSession(player);
        if (session == null) return;

        if (session.bothReady() && !session.isConfirmed()) {
            // 倒计时中关闭 → 取消倒计时，取消交易，通知双方
            session.getView().runnable.cancel();
            TradeManager.cancelTrade(player);
        } else if (session.isConfirmed()) {
            // 交易已完成的边界情况
            TradeManager.cancelTrade(session);
        } else {
            // 双方未确认但有一方提前结束
            TradeManager.cancelTrade(player);
        }
    }

    private static final Map<UUID, Long> interactCooldown = Collections.synchronizedMap(new ConcurrentHashMap<>());
    private static final long INTERACT_DELAY = 1000; // 1秒内 内防止重复触发

    //玩家交互事件
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!GoodsTrade.config.isEnabledShiftClick()) return;
        if (event.getRightClicked() instanceof Player) {
            Player senderPlayer = event.getPlayer();
            if (senderPlayer.isSneaking()) {
                UUID playerId = senderPlayer.getUniqueId();
                long currentTime = System.currentTimeMillis();

                // 检查冷却时间
                if (interactCooldown.containsKey(playerId)) {
                    long lastInteractTime = interactCooldown.get(playerId);
                    if (currentTime - lastInteractTime < INTERACT_DELAY) {
                        event.setCancelled(true);
                        return;
                    }
                }

                // 更新冷却时间
                interactCooldown.put(playerId, currentTime);


                Player targetPlayer = (Player) event.getRightClicked();
                TradeManager.sendTradeRequest(senderPlayer, targetPlayer);
            }
        }
    }

    // 伤害保护事件
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!GoodsTrade.config.isSafeDamage()) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (TradeManager.isTrade(player)) {
            event.setCancelled(true);
        }
    }

    // 实体伤害保护事件（防止被其他实体伤害）
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!GoodsTrade.config.isSafeDamage()) return;

        // 检查受到伤害的玩家
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (TradeManager.isTrade(player)) {
                event.setCancelled(true);
                event.getDamager().sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("protection.trading"));
                return;
            }
        }

        // 检查造成伤害的玩家，防止交易中的玩家对别人造成伤害（真的会有这种情况吗？）
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if (TradeManager.isTrade(damager)) {
                event.setCancelled(true);
            }
        }
    }

    // 移动保护事件
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!GoodsTrade.config.isSafeMove()) return;
        Player player = event.getPlayer();

        if (TradeManager.isTrade(player)) {
            // 如果位置发生了变化
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                    event.getFrom().getBlockZ() != event.getTo().getBlockZ() ||
                    event.getFrom().getBlockY() != event.getTo().getBlockY()) {
                event.setCancelled(true);
            }
        }
    }

}
