package cc.sbsj.polang.goodstrade.listener;

import cc.sbsj.polang.goodstrade.gui.Gui;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import cc.sbsj.polang.goodstrade.util.Utils;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import pers.neige.neigeitems.annotation.Listener;

public class InventoryCloseListener {
    @Listener
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof Gui)) return;
        val player = (Player) event.getPlayer();
        //返还手里物品
        val cursorItem = player.getOpenInventory().getCursor();
        if (Utils.isItemStackNotEmpty(cursorItem)) {
            Utils.addItems(player, cursorItem);
        }
        if (event.getReason() == InventoryCloseEvent.Reason.PLUGIN) {
            return;
        }
        //该玩家是否正在交易
        if (!TradeManager.isTrade(player)) return;
        val session = TradeManager.getSession(player);
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
}
