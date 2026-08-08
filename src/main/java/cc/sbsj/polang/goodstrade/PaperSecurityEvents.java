package cc.sbsj.polang.goodstrade;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Paper-only security listeners.
 *
 * This class is loaded only when the Paper event API is available. The main
 * Bukkit listener remains usable on Spigot-derived servers such as CatServer.
 */
public final class PaperSecurityEvents implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onTabComplete(AsyncTabCompleteEvent event) {
        String buffer = event.getBuffer();
        if (!buffer.contains("@") || !buffer.contains("[nbt=")) {
            return;
        }

        event.setCancelled(true);
        if (!(event.getSender() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getSender();
        Bukkit.getScheduler().runTask(GoodsTrade.instance, () ->
                player.kickPlayer(GoodsTrade.getPrefix()
                        + GoodsTrade.lang.getString("security.exploit-attempt")));
    }
}
