package cc.sbsj.polang.goodstrade.listener;

import cc.sbsj.polang.goodstrade.gui.Gui;
import cc.sbsj.polang.goodstrade.gui.view.View;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import pers.neige.neigeitems.annotation.Listener;

public class InventoryClickListener {
    @Listener
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null ||
            event.getClick() == ClickType.UNKNOWN ||
            event.getClick() == ClickType.WINDOW_BORDER_RIGHT ||
            event.getClick() == ClickType.WINDOW_BORDER_LEFT ||
            event.getAction() == InventoryAction.UNKNOWN) {
            return; // 忽略无效点击
        }
        val player = (Player) event.getWhoClicked();

        // 判断是不是这个插件的gui
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof Gui gui)) return;

        val session = TradeManager.getSession(player);
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
            val slot = event.getRawSlot();
            if (gui.buttons[slot] != null) {
                gui.buttons[slot].onClick(event);
            } else {
                event.setCancelled(false);
            }
        }
    }
}
