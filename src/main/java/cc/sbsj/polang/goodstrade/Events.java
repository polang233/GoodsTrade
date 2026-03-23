package cc.sbsj.polang.goodstrade;

import cc.sbsj.polang.goodstrade.gui.Gui;
import cc.sbsj.polang.goodstrade.gui.view.View;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import cc.sbsj.polang.goodstrade.trade.TradeSession;
import cc.sbsj.polang.goodstrade.util.Utils;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

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
        if (session.bothReady() && !View.readySlots.contains(event.getRawSlot())) {
            session.getSenderPlayer().sendMessage("§c正在交易确认中，若需取消请按取消按钮！");
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
        Player sender = session.getSenderPlayer();
        Player target = session.getTargetPlayer();
        if (player == sender) {
            //阻止发起者操作被发起者界面
            for (int rawSlot : event.getRawSlots()) {
                if (View.isTargetTradeSlot(rawSlot)) {
                    event.setCancelled(true);
                    return;
                } else if (View.isSenderTradeSlot(rawSlot)) {
                    if (session.isSenderReady()) {
                        event.setCancelled(true);
                        sender.sendMessage("§c你已确认，物品状态锁定！");
                        sender.playSound(sender.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                        return;
                    }
                }
            }

        } else {
            for (int rawSlot : event.getRawSlots()) {
                if (View.isSenderTradeSlot(rawSlot)) {
                    event.setCancelled(true);
                    return;
                } else if (View.isTargetTradeSlot(rawSlot)) {
                    if (session.isTargetReady()) {
                        event.setCancelled(true);
                        target.sendMessage("§c你已确认，物品状态锁定！");
                        target.playSound(target.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                        return;
                    }
                }

            }

        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof Gui)) return;
        Player player = (Player) event.getPlayer();
        //返还手里物品
        ItemStack cursorItem = player.getOpenInventory().getCursor();
        if (Utils.isItemStackNotEmpty(cursorItem)) {
            Utils.addItems(player, cursorItem);
        }
        if (event.getReason() == InventoryCloseEvent.Reason.PLUGIN) {
            return;
        }
        //该玩家是否正在交易
        if (!TradeManager.isTrade(player)) return;
        TradeSession session = TradeManager.getSession(player);
        if (session == null) return;
        //俩人都确认并且没完成，有人提前想结束
        if (session.bothReady() && !session.isConfirmed()) {
            //取消倒计时
            session.getView().runnable.cancel();
            //取消交易
            TradeManager.cancelTrade(player);
        } else {
            if (session.isConfirmed()) {
                //已经结束的关闭界面
                TradeManager.cancelTrade(session);
            } else {
                //正在交易但关闭界面视为提前结束
                TradeManager.cancelTrade(player);
            }
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

    //帮忙修复简单漏洞（1.18、1.20均有出现）
    @EventHandler(ignoreCancelled = true)
    public void onTabComplete(AsyncTabCompleteEvent event) {
        String buffer = event.getBuffer();
        if (buffer.contains("@") && buffer.contains("[nbt=")) {
            event.setCancelled(true);
            Player player = (Player) event.getSender();
            //主线程踢出
            Bukkit.getScheduler().runTask(GoodsTrade.instance, () -> {
                Component kick = Component.text(GoodsTrade.PREFIX + "§c试图滥发消息被踢出！");
                player.kick(kick);
            });
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
                event.getDamager().sendMessage("该玩家正在交易，你无法对其造成伤害");
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
