package cc.sbsj.polang.goodstrade.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiButton {
    public int slot;
    public ItemStack buttonItemStack;
    public boolean isCancelEvent = true;

    public GuiButton(int slot, ItemStack buttonItemStack) {
        this.slot = slot;
        this.buttonItemStack = buttonItemStack;
    }

    public GuiButton(ItemStack buttonItemStack) {
        if (buttonItemStack == null) {
            this.buttonItemStack = ViewHelper.air;
            this.isCancelEvent = false;
            return;
        }
        this.buttonItemStack = buttonItemStack;
    }


    public GuiButton(int slot) {
        this.slot = slot;
        this.buttonItemStack = ViewHelper.air;
    }

    private GuiButtonClickHandler clickHandler;
    private GuiButtonClickHandler shiftClickHandler;

    public void setOnClick(GuiButtonClickHandler handler) {
        this.clickHandler = handler;
    }

    public void setOnShiftClick(GuiButtonClickHandler handler) {
        this.shiftClickHandler = handler;
    }

    public void onClick(InventoryClickEvent event) {
        if (shiftClickHandler != null && event.isShiftClick()) {
            shiftClickHandler.handle(event);
            return;
        }
        if (clickHandler != null) {
            clickHandler.handle(event);
        }

    }
}
