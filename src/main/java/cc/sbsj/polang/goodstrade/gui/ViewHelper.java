package cc.sbsj.polang.goodstrade.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ViewHelper {
    public final static ItemStack air = new ItemStack(Material.AIR);
    public final static ItemStack backGround = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);

    public final GuiButton senderReadyButton = new GuiButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14));

    public final GuiButton senderReadyButtonYes = new GuiButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));

    public final GuiButton senderReadyButtonWait = new GuiButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4));

    public final GuiButton targetReadyButton = new GuiButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14));
    public final GuiButton targetReadyButtonYes = new GuiButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
    public final GuiButton targetReadyButtonWait = new GuiButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4));

    static {

    }
}
