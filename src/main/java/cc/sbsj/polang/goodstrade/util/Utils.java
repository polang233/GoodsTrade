package cc.sbsj.polang.goodstrade.util;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Utils {
    static public boolean isItemStackNotEmpty(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR;
    }

    //给玩家发物品到背包，如果包满了就丢地上
    public static void addItems(Player player, ItemStack... items) {
        if (items.length == 0) return;
        Map<Integer, ItemStack> excessItems = player.getInventory().addItem(items);
        //没有需要返还的物品提前跳过
        if (excessItems.isEmpty()) return;
        for (Map.Entry<Integer, ItemStack> entry : excessItems.entrySet()) {
            player.getWorld().dropItem(player.getLocation(), entry.getValue());
        }
        player.sendMessage(GoodsTrade.PREFIX + "§c你的背包已满！多余物品已丢出");
    }

    //有点难处理玩家ID过长情况，先不管了
    public static String createTwoPlayerTitle(String name1, String name2) {
        int totalLength = 35;
        // 如果两个名字总长度超过总长度，使用简化格式
        if (name1.length() + name2.length() > totalLength) {
            return name1.substring(0, Math.min(name1.length(), 10)) + " " +
                    name2.substring(0, Math.min(name2.length(), 10));
        }

        // 计算中间空格数（至少保留 3 个空格）
        int middleSpace = Math.max(3, totalLength - name1.length() - name2.length());

        // 计算实际使用的总长度
        int actualLength = name1.length() + middleSpace + name2.length();

        // 如果还有剩余空间，平均分配到两侧
        int extraSpace = totalLength - actualLength;
        int leftPadding = extraSpace / 2;
        int rightPadding = extraSpace - leftPadding;

        StringBuilder sb = new StringBuilder();

        // 左侧填充
        for (int i = 0; i < Math.max(0, leftPadding); i++) {
            sb.append(" ");
        }

        // 玩家 1 名字
        sb.append(name1);

        // 中间空格
        for (int i = 0; i < middleSpace; i++) {
            sb.append(" ");
        }

        // 玩家 2 名字
        sb.append(name2);

        // 右侧填充
        for (int i = 0; i < Math.max(0, rightPadding); i++) {
            sb.append(" ");
        }

        return sb.toString();
    }
}
