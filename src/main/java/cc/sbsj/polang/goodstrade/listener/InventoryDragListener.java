package cc.sbsj.polang.goodstrade.listener;

import cc.sbsj.polang.goodstrade.gui.Gui;
import cc.sbsj.polang.goodstrade.gui.view.View;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import lombok.val;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import pers.neige.neigeitems.annotation.Listener;

public class InventoryDragListener {
    @Listener
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof Gui)) return;
        val player = (Player) event.getWhoClicked();
        val session = TradeManager.getSession(player);
        val sender = session.getSenderPlayer();
        val target = session.getTargetPlayer();
        if (player == sender) {
            //阻止发起者操作被发起者界面
            for (var rawSlot : event.getRawSlots()) {
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
            for (var rawSlot : event.getRawSlots()) {
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
}
