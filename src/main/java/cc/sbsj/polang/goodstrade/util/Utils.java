package cc.sbsj.polang.goodstrade.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Utils {
    static public boolean isItemStackEmpty(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR;
    }
}
