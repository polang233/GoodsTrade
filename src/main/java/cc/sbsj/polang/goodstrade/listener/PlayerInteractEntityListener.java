package cc.sbsj.polang.goodstrade.listener;

import cc.sbsj.polang.goodstrade.manager.ConfigManager;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import pers.neige.neigeitems.annotation.Listener;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInteractEntityListener {
    private static final Map<UUID, Long> interactCooldown = Collections.synchronizedMap(new ConcurrentHashMap<>());
    private static final long INTERACT_DELAY = 1000; // 1秒内 内防止重复触发

    @Listener
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!ConfigManager.getShiftClickTrade()) return;
        if (event.getRightClicked() instanceof Player targetPlayer) {
            val senderPlayer = event.getPlayer();
            if (senderPlayer.isSneaking()) {
                val playerId = senderPlayer.getUniqueId();
                val currentTime = System.currentTimeMillis();

                // 检查冷却时间
                if (interactCooldown.containsKey(playerId)) {
                    val lastInteractTime = interactCooldown.get(playerId);
                    if (currentTime - lastInteractTime < INTERACT_DELAY) {
                        event.setCancelled(true);
                        return;
                    }
                }

                // 更新冷却时间
                interactCooldown.put(playerId, currentTime);

                TradeManager.sendTradeRequest(senderPlayer, targetPlayer);
            }
        }
    }
}
