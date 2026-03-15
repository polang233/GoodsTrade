package cc.sbsj.polang.goodstrade.util;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Utils {
    static public boolean isItemStackEmpty(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR;
    }

    //给玩家发物品到背包，如果包满了就丢地上
    public static void addItems(Player player, ItemStack... items) {
        if (items.length == 0) return;
        Map<Integer, ItemStack> excessItems = player.getInventory().addItem(items);
        //没有需要返还的物品提前跳过
        if (excessItems == null || excessItems.isEmpty()) return;
        for (Map.Entry<Integer, ItemStack> entry : excessItems.entrySet()) {
            player.getWorld().dropItem(player.getLocation(), entry.getValue());
        }
        player.sendMessage(GoodsTrade.PREFIX + "§c你的背包已满！多余物品已丢出");
    }
}
