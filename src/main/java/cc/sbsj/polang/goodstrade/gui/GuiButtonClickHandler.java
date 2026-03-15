package cc.sbsj.polang.goodstrade.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

//动态增加点击事件的代码接口
@FunctionalInterface
public interface GuiButtonClickHandler {
    void handle(InventoryClickEvent event);
}
