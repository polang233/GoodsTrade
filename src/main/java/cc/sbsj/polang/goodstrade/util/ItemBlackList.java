package cc.sbsj.polang.goodstrade.util;

import cc.sbsj.polang.goodstrade.gui.view.View;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import cc.sbsj.polang.goodstrade.trade.TradeSession;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ItemBlackList {
    private final boolean enabled;
    private final List<String> loreRules;
    private final List<String> nameRules;
    private final NbtBlackList nbtBlackList;

    private ItemBlackList(boolean enabled, List<String> loreRules, List<String> nameRules, NbtBlackList nbtBlackList) {
        this.enabled = enabled;
        this.loreRules = loreRules;
        this.nameRules = nameRules;
        this.nbtBlackList = nbtBlackList;
    }

    public static ItemBlackList fromConfig(Configuration config) {
        return new ItemBlackList(
                config.getBoolean("Trade.Item-BlackList.Enable", false),
                safeList(config.getStringList("Trade.Item-BlackList.Lore")),
                safeList(config.getStringList("Trade.Item-BlackList.Name")),
                NbtBlackList.fromConfig(config.getStringList("Trade.Item-BlackList.NBT"))
        );
    }

    public boolean contains(Player player, boolean senderSide) {
        if (!enabled) return false;
        TradeSession session = TradeManager.getSession(player);
        if (session == null) return false;

        List<Integer> slots = senderSide ? View.senderTradeSlots : View.targetTradeSlots;
        return contains(slots, session);
    }

    private boolean contains(List<Integer> slots, TradeSession session) {
        for (int slot : slots) {
            ItemStack item = session.getView().gui.getInventory().getItem(slot);
            if (!Utils.isItemStackNotEmpty(item)) continue;
            if (isNameBlackList(item) || isLoreBlackList(item) || nbtBlackList.contains(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNameBlackList(ItemStack item) {
        if (nameRules.isEmpty() || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;
        String displayName = item.getItemMeta().getDisplayName();
        for (String rule : nameRules) {
            if (displayName.contains(rule)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLoreBlackList(ItemStack item) {
        if (loreRules.isEmpty() || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
        for (String lore : item.getItemMeta().getLore()) {
            for (String rule : loreRules) {
                if (lore.contains(rule)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<String> safeList(List<String> list) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list;
    }
}
