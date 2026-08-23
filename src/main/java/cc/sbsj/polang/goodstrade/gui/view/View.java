package cc.sbsj.polang.goodstrade.gui.view;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.gui.GuiButton;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * 界面的公共内容都塞这里
 */
public class View {
    public final static ItemStack air = new ItemStack(Material.AIR);
    public static ItemStack backGround = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();

    public static ItemStack senderReadyButtonItem = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
    public static ItemStack senderReadyButtonItemYes = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
    public static ItemStack senderReadyButtonItemWait = XMaterial.BLUE_STAINED_GLASS_PANE.parseItem();

    public static ItemStack targetReadyButtonItem = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
    public static ItemStack targetReadyButtonItemYes = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
    public static ItemStack targetReadyButtonItemWait = XMaterial.BLUE_STAINED_GLASS_PANE.parseItem();

    public static ItemStack cancelReadyItem = XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem();
    public static ItemStack infoItem = XMaterial.IRON_BARS.parseItem();
    public static final List<ItemStack> moneyButtonItems = new ArrayList<>();


    public final GuiButton senderReadyButton = new GuiButton(senderReadyButtonItem.clone());
    public final GuiButton senderReadyButtonYes = new GuiButton(senderReadyButtonItemYes.clone());
    public final GuiButton senderReadyButtonWait = new GuiButton(senderReadyButtonItemWait.clone());

    public final GuiButton targetReadyButton = new GuiButton(targetReadyButtonItem.clone());
    public final GuiButton targetReadyButtonYes = new GuiButton(targetReadyButtonItemYes.clone());
    public final GuiButton targetReadyButtonWait = new GuiButton(targetReadyButtonItemWait.clone());

    public final GuiButton cancelReadyButton = new GuiButton(cancelReadyItem.clone());
    public final GuiButton infoButton = new GuiButton(infoItem.clone());


    public static final List<Integer> senderTradeSlots = new ArrayList<>();
    public static final List<Integer> targetTradeSlots = new ArrayList<>();
    public static final Set<Integer> readySlots = new HashSet<>(Arrays.asList(46, 47, 48, 50, 51, 52));


    static {
        defaultTradingSlots();
        resetDefaultItems();
    }

    public static void resetDefaultItems() {
        backGround = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
        senderReadyButtonItem = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        senderReadyButtonItemYes = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
        senderReadyButtonItemWait = XMaterial.BLUE_STAINED_GLASS_PANE.parseItem();
        targetReadyButtonItem = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        targetReadyButtonItemYes = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
        targetReadyButtonItemWait = XMaterial.BLUE_STAINED_GLASS_PANE.parseItem();
        cancelReadyItem = XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem();
        infoItem = XMaterial.IRON_BARS.parseItem();
        moneyButtonItems.clear();
        for (int i = 0; i < 4; i++) {
            moneyButtonItems.add(XMaterial.GOLD_INGOT.parseItem());
        }

        defaultItemBackGround();
        defaultItemReady();
        defaultItemReadyYes();
        defaultItemReadyWait();
        defaultTargetItemReady();
        defaultTargetItemReadyYes();
        defaultTargetItemReadyWait();
        defaultItemCancelReady();
        defaultItemInfo();
        defaultMoneyButtons();
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
        ItemMeta meta = senderReadyButtonItem.getItemMeta();
        meta.setDisplayName(text("trade-view.unconfirmed-name", "§7当前未确认"));
        List<String> lore = lines("trade-view.click-confirm-lore", Arrays.asList("", "§a单击可确认"));
        meta.setLore(lore);
        senderReadyButtonItem.setItemMeta(meta);
    }

    public static void defaultItemReadyYes() {
        ItemMeta meta = senderReadyButtonItemYes.getItemMeta();
        meta.setDisplayName(text("trade-view.confirmed-name", "§e已确认"));
        List<String> lore = lines("trade-view.waiting-other-lore", Arrays.asList("", "§a等待对方确认"));
        meta.setLore(lore);
        senderReadyButtonItemYes.setItemMeta(meta);
    }

    public static void defaultItemReadyWait() {
        ItemMeta meta = senderReadyButtonItemWait.getItemMeta();
        meta.setDisplayName(text("trade-view.countdown-name", "§b确认交易中..."));
        List<String> lore = lines("trade-view.countdown-lore", Arrays.asList("", "§e可检查对方物品是否符合要求", "§6若存在问题请再次点击以取消确认！"));
        meta.setLore(lore);
        senderReadyButtonItemWait.setItemMeta(meta);
    }

    public static void defaultTargetItemReady() {
        ItemMeta meta = targetReadyButtonItem.getItemMeta();
        meta.setDisplayName(text("trade-view.unconfirmed-name", "§7当前未确认"));
        List<String> lore = lines("trade-view.click-confirm-lore", Arrays.asList("", "§a单击可确认"));
        meta.setLore(lore);
        targetReadyButtonItem.setItemMeta(meta);
    }

    public static void defaultTargetItemReadyYes() {
        ItemMeta meta = targetReadyButtonItemYes.getItemMeta();
        meta.setDisplayName(text("trade-view.confirmed-name", "§e已确认"));
        List<String> lore = lines("trade-view.waiting-other-lore", Arrays.asList("", "§a等待对方确认"));
        meta.setLore(lore);
        targetReadyButtonItemYes.setItemMeta(meta);
    }

    public static void defaultTargetItemReadyWait() {
        ItemMeta meta = targetReadyButtonItemWait.getItemMeta();
        meta.setDisplayName(text("trade-view.countdown-name", "§b确认交易中..."));
        List<String> lore = lines("trade-view.countdown-lore", Arrays.asList("", "§e可检查对方物品是否符合要求", "§6若存在问题请再次点击以取消确认！"));
        meta.setLore(lore);
        targetReadyButtonItemWait.setItemMeta(meta);
    }

    public static void defaultItemCancelReady() {
        ItemMeta meta = cancelReadyItem.getItemMeta();
        meta.setDisplayName(text("trade-view.other-cancelled-name", "§c对方取消"));
        List<String> lore = lines("trade-view.restart-confirm-lore", Arrays.asList("", "§e再次点击重新开始，需要双方都确认才可进行交易"));
        meta.setLore(lore);
        cancelReadyItem.setItemMeta(meta);
    }

    public static void defaultItemInfo() {
        ItemMeta meta = infoItem.getItemMeta();
        meta.setDisplayName(text("trade-view.divider-name", "§7分隔板"));
        List<String> lore = lines("trade-view.divider-lore", Arrays.asList("",
                "§a交易发起者默认在左侧",
                "§a被发起者默认在右侧",
                "§e将想要交易的物品放入后点击下方确认",
                "§e确认后将锁定物品，待双方确认进行交易",
                "§e交易过程中等待读秒结束，期间请检查物品"
        ));
        meta.setLore(lore);
        infoItem.setItemMeta(meta);
    }

    public static void defaultMoneyButtons() {
        for (ItemStack item : moneyButtonItems) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(text("trade-view.money-button-name", "§6调整金币: §e%amount%"));
            meta.setLore(lines("trade-view.money-button-lore", Arrays.asList(
                    "",
                    "§a左键: 增加 %amount%",
                    "§c右键: 减少 %amount%"
            )));
            item.setItemMeta(meta);
        }
    }

    private static String text(String path, String fallback) {
        return GoodsTrade.lang == null ? fallback : GoodsTrade.lang.getString(path, fallback);
    }

    private static List<String> lines(String path, List<String> fallback) {
        if (GoodsTrade.lang == null) return fallback;
        List<String> configured = GoodsTrade.lang.getStringList(path);
        return configured.isEmpty() ? fallback : configured;
    }

    public boolean isBlackList(InventoryClickEvent event, Player player, boolean senderSide) {
        if (GoodsTrade.config.getItemBlackList().contains(player, senderSide)) {
            player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.blacklist-hit"));
            event.setCancelled(true);
            return true;
        }
        return false;
    }
}
