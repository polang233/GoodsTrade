package cc.sbsj.polang.goodstrade.config;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.gui.view.View;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import cc.sbsj.polang.goodstrade.trade.TradeSession;
import cc.sbsj.polang.goodstrade.util.Utils;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Config {
    public Configuration config;
    public GoodsTrade plugin;

    public Config(GoodsTrade plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
    }

    public int getWaitTime() {
        //验证下有没有过64，省的爆掉了物品堆叠
        int waitTime = config.getInt("Trade.Wait-Time", 5);
        if (waitTime > 64) {
            waitTime = 64;
        }
        return waitTime;
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();


    }

    public boolean isEnabledShiftClick() {
        return config.getBoolean("Trade.Triggers.Shift-Right-Click", false);
    }

    public boolean isSafeDamage() {
        return config.getBoolean("Trade.Safe.Damage", false);
    }

    public boolean isSafeMove() {
        return config.getBoolean("Trade.Safe.Move", false);
    }

    public boolean isBlackList() {
        return config.getBoolean("Trade.Item-BlackList.Enable", false);
    }

    public boolean isTradeLoreToBlackList(Player player) {
        List<String> blackList = config.getStringList("Trade.Item-BlackList.Lore");
        if (blackList.isEmpty()) return false;
        TradeSession session = TradeManager.getSession(player);

        if (session.getSenderPlayer() == player) {
            return isLoreBlackList(View.senderTradeSlots, session, blackList);
        } else {
            return isLoreBlackList(View.targetTradeSlots, session, blackList);
        }
    }

    public boolean isTradeNameToBlackList(Player player) {
        List<String> blackList = config.getStringList("Trade.Item-BlackList.Name");
        if (blackList.isEmpty()) return false;
        TradeSession session = TradeManager.getSession(player);

        if (session.getSenderPlayer() == player) {
            return isNameBlackList(View.senderTradeSlots, session, blackList);
        } else {
            return isNameBlackList(View.targetTradeSlots, session, blackList);
        }
    }

    private static boolean isNameBlackList(List<Integer> slots, TradeSession session, List<String> blackList) {
        for (int i : slots) {
            ItemStack item = session.getView().gui.getInventory().getItem(i);
            if (!Utils.isItemStackNotEmpty(item)) continue;
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                for (String blackStr : blackList) {
                    if (item.getItemMeta().getDisplayName().contains(blackStr)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isLoreBlackList(List<Integer> slots, TradeSession session, List<String> blackList) {
        for (int i : slots) {
            ItemStack item = session.getView().gui.getInventory().getItem(i);
            if (!Utils.isItemStackNotEmpty(item)) continue;
            if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                for (String lore : item.getItemMeta().getLore()) {
                    for (String blackStr : blackList) {
                        if (lore.contains(blackStr)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
