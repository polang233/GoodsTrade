package cc.sbsj.polang.goodstrade.gui;

import cc.sbsj.polang.goodstrade.gui.view.View;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public class Gui implements InventoryHolder {
    private Player player;      //主要玩家
    public GuiButton[] buttons; //所有按钮
    private Inventory inv;      //界面
    private String title;       //标题
    private int row;            //列数
    private int size;           //界面总大小


    public Gui(Player player, String title, int row) {
        this.player = player;
        this.title = title;
        this.row = row;
        this.size = row * 9;
        this.buttons = new GuiButton[size];
        this.inv = Bukkit.createInventory(this, size, title);
    }

    public Gui(Player player, String title, InventoryType type) {
        this.player = player;
        this.title = title;
        this.row = type.getDefaultSize();
        this.buttons = new GuiButton[row];
        this.inv = Bukkit.createInventory(this, type, title);
    }


    public Gui(Player player, String title, int size, boolean isAddBackground) {
        this.player = player;
        this.title = title;
        this.row = size;
        this.buttons = new GuiButton[size];
        this.inv = Bukkit.createInventory(this, size, title);
    }

    // 整个界面添加背景
    public void addAllBackGround() {
        GuiButton background = new GuiButton(View.backGround);
        for (int i = 0; i < row * 9; ++i) {
            addButton(i, background);
        }
    }

    //一般用于空气
    public void addButton(GuiButton button) {
        this.buttons[button.slot] = button;
        this.inv.setItem(button.slot, button.buttonItemStack);
    }

    public void addButton(int slot, GuiButton button) {
        button.slot = slot;
        this.buttons[slot] = button;
        this.inv.setItem(slot, button.buttonItemStack);
    }


    /**
     * @param x 横坐标
     * @param y 纵坐标
     *          表示界面中的位置，从1开始计算符合直觉
     */
    public void addButton(int x, int y, GuiButton button) {
        int slot = (y - 1) * 9 + x - 1;
        button.slot = slot;
        this.buttons[slot] = button;
        this.inv.setItem(slot, button.buttonItemStack);
    }

    public void addAir(int slot) {
        this.inv.setItem(slot, View.air);
        this.buttons[slot] = null;
    }

    public void open(Player player) {
        player.openInventory(inv);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
