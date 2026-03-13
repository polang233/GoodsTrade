package cc.sbsj.polang.goodstrade;

import cc.sbsj.polang.goodstrade.gui.Gui;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import cc.sbsj.polang.goodstrade.trade.TradeSession;
import cc.sbsj.polang.goodstrade.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null ||
                event.getCurrentItem() == null ||
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


        // 点击的是玩家自己背包就
        player.sendMessage(event.getClick().toString());
        if (event.getClickedInventory() == event.getView().getBottomInventory()) {
            //TODO
            //安全处理需要全部排查
            switch (event.getClick()) {
                case SHIFT_RIGHT:
                case SHIFT_LEFT:
                    event.setCancelled(true);
                    break;
            }
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
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof Gui)) return;
        Player player = (Player) event.getPlayer();
        //返还手里物品
        ItemStack cursorItem = player.getOpenInventory().getCursor();
        if (Utils.isItemStackEmpty(cursorItem)) {
            TradeManager.addItems(player, cursorItem);
        }
        //该玩家是否正在交易
        if (TradeManager.isTrade(player)) {
            TradeSession session = TradeManager.getSession(player);
            if (session.isConfirmed()) {
                //已经结束的关闭界面
                TradeManager.cancelTrade(session);
            } else {
                //正在交易但关闭界面视为提前结束
                TradeManager.cancelTrade(player);
            }

        }
    }
}
