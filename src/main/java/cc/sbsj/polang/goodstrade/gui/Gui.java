package cc.sbsj.polang.goodstrade.gui;

import cc.sbsj.polang.goodstrade.gui.view.View;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
public class Gui implements InventoryHolder {
    public GuiButton[] buttons; //所有按钮
    private Player player;      //主要玩家
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

    // 整个界面添加背景
    public void addAllBackGround() {
        GuiButton background = new GuiButton(View.backGround);
        for (int i = 0; i < row * 9; ++i) {
            addButton(i, background);
        }
    }
    //添加按钮

    public void addButton(int slot, GuiButton button) {
        button.slot = slot;
        this.buttons[slot] = button;
        this.inv.setItem(slot, button.buttonItemStack);
    }

    //给玩家打开这个界面
    public void open(Player player) {
        player.openInventory(inv);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
