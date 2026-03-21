package cc.sbsj.polang.goodstrade.listener;

import cc.sbsj.polang.goodstrade.manager.ConfigManager;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import lombok.val;
import org.bukkit.event.player.PlayerMoveEvent;
import pers.neige.neigeitems.annotation.Listener;

public class PlayerMoveListener {
    @Listener
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!ConfigManager.getPreventMoveWhenTrade()) return;
        val player = event.getPlayer();

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
