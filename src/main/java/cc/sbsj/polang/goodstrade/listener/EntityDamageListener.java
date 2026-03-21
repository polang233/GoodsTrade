package cc.sbsj.polang.goodstrade.listener;

import cc.sbsj.polang.goodstrade.manager.ConfigManager;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import pers.neige.neigeitems.annotation.Listener;

public class EntityDamageListener {
    @Listener
    public void onEntityDamage(EntityDamageEvent event) {
        if (!ConfigManager.getPreventDamageWhenTrade()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (TradeManager.isTrade(player)) {
            event.setCancelled(true);
        }
    }
}
