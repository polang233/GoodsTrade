package cc.sbsj.polang.goodstrade.gui.view;

import cc.sbsj.polang.goodstrade.gui.GuiButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class View {
    public final static ItemStack air = new ItemStack(Material.AIR);
    public final static ItemStack backGround = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);

    public static ItemStack readyButtonItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
    public static ItemStack readyButtonItemYes = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
    public static ItemStack readyButtonItemWait = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);

    public static ItemStack cancelReadyItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
    public static ItemStack infoItem = new ItemStack(Material.IRON_FENCE);


    public final GuiButton senderReadyButton = new GuiButton(readyButtonItem);
    public final GuiButton senderReadyButtonYes = new GuiButton(readyButtonItemYes);
    public final GuiButton senderReadyButtonWait = new GuiButton(readyButtonItemWait);

    public final GuiButton targetReadyButton = new GuiButton(readyButtonItem);
    public final GuiButton targetReadyButtonYes = new GuiButton(readyButtonItemYes);
    public final GuiButton targetReadyButtonWait = new GuiButton(readyButtonItemWait);

    public final GuiButton cancelReadyButton = new GuiButton(cancelReadyItem);
    public final GuiButton infoButton = new GuiButton(infoItem);


    public static final List<Integer> senderTradeSlots = new ArrayList<>();
    public static final List<Integer> targetTradeSlots = new ArrayList<>();
    public static final Set<Integer> readySlots = new HashSet<>(Arrays.asList(46, 47, 48, 50, 51, 52));


    static {
        defaultTradingSlots();
        defaultItemBackGround();
        defaultItemReady();
        defaultItemReadyYes();
        defaultItemReadyWait();
        defaultItemCancelReady();
        defaultItemInfo();
    }

    public static void defaultTradingSlots() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 9; j++) {
                switch (j) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        senderTradeSlots.add(j + i * 9);
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        targetTradeSlots.add(j + i * 9);
                }
            }
        }
    }

    public static boolean isTradeSlot(int slot) {
        return senderTradeSlots.contains(slot) || targetTradeSlots.contains(slot);
    }

    public static boolean isSenderTradeSlot(int slot) {
        return senderTradeSlots.contains(slot);
    }

    public static boolean isTargetTradeSlot(int slot) {
        return targetTradeSlots.contains(slot);
    }

    public static void defaultItemBackGround() {
        ItemMeta meta = backGround.getItemMeta();
        meta.setDisplayName(" ");
        List<String> lore = Arrays.asList(" ");
        meta.setLore(lore);
        backGround.setItemMeta(meta);
    }

    public static void defaultItemReady() {
        ItemMeta meta = readyButtonItem.getItemMeta();
        meta.setDisplayName("§7当前未确认");
        List<String> lore = Arrays.asList("", "§a单击可确认");
        meta.setLore(lore);
        readyButtonItem.setItemMeta(meta);
    }

    public static void defaultItemReadyYes() {
        ItemMeta meta = readyButtonItemYes.getItemMeta();
        meta.setDisplayName("§e已确认");
        List<String> lore = Arrays.asList("", "§a等待对方确认");
        meta.setLore(lore);
        readyButtonItemYes.setItemMeta(meta);
    }

    public static void defaultItemReadyWait() {
        ItemMeta meta = readyButtonItemWait.getItemMeta();
        meta.setDisplayName("§b确认交易中...");
        List<String> lore = Arrays.asList("", "§e可检查对方物品是否符合要求", "§6若存在问题请再次点击以取消确认！");
        meta.setLore(lore);
        readyButtonItemWait.setItemMeta(meta);
    }

    public static void defaultItemCancelReady() {
        ItemMeta meta = cancelReadyItem.getItemMeta();
        meta.setDisplayName("§c对方取消");
        List<String> lore = Arrays.asList("", "§e再次点击重新开始，需要双方都确认才可进行交易");
        meta.setLore(lore);
        cancelReadyItem.setItemMeta(meta);
    }

    public static void defaultItemInfo() {
        ItemMeta meta = infoItem.getItemMeta();
        meta.setDisplayName("§7分隔板");
        List<String> lore = Arrays.asList("",
                "§a交易发起者默认在左侧",
                "§a被发起者默认在右侧",
                "§e将想要交易的物品放入后点击下方确认",
                "§e确认后将锁定物品，待双方确认进行交易",
                "§e交易过程中等待读秒结束，期间请检查物品"
        );
        meta.setLore(lore);
        infoItem.setItemMeta(meta);
    }
}
