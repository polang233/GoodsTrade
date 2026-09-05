package cc.sbsj.polang.goodstrade.hook.placeholder;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Papi extends PlaceholderExpansion {

    private final GoodsTrade plugin;

    public Papi(GoodsTrade plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "goodstrade";
    }

    @Override
    public @NotNull String getAuthor() {
        return "polang";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("stats")) {
            if (player == null) {
                return "false";
            }
            boolean accept = GoodsTrade.playerDataManager.isTradeAccept(player.getUniqueId());
            return String.valueOf(accept);
        }
        return null;
    }
}
