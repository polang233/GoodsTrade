package cc.sbsj.polang.goodstrade.listener;

import cc.sbsj.polang.goodstrade.manager.ConfigManager;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pers.neige.neigeitems.annotation.Listener;

public class EntityDamageByEntityListener {
    @Listener
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!ConfigManager.getPreventDamageWhenTrade()) return;

        // 检查受到伤害的玩家
        if (event.getEntity() instanceof Player player) {
            if (TradeManager.isTrade(player)) {
                event.setCancelled(true);
                event.getDamager().sendMessage("该玩家正在交易，你无法对其造成伤害");
                return;
            }
        }

        // 检查造成伤害的玩家，防止交易中的玩家对别人造成伤害（真的会有这种情况吗？）
        if (event.getDamager() instanceof Player damager) {
            if (TradeManager.isTrade(damager)) {
                event.setCancelled(true);
            }
        }
    }
}
