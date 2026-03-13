package cc.sbsj.polang.goodstrade.gui.view;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.gui.Gui;
import cc.sbsj.polang.goodstrade.gui.GuiButton;
import cc.sbsj.polang.goodstrade.gui.ViewHelper;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import cc.sbsj.polang.goodstrade.trade.TradeSession;
import cc.sbsj.polang.goodstrade.util.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/*
    ~~~~@~~~~
    ~~~~@~~~~
    ~~~~@~~~~
    ~~~~@~~~~
    #FFF#TTT#
    ####Q####

    ~=操作格
    #=背景物品
    @=中间提示物品
    F=交易发起者的确认状态
    T=交易接受者的确认状态
    Q=确认交易按钮
*/
public class TradeView extends ViewHelper {
    TradeSession session;
    Gui gui;

    public void open(Player sender, Player target) {
        // 创建交易会话
        session = TradeManager.createSession(sender, target, this);
        // 创建 GUI 界面
        gui = new Gui(sender, createTitle(sender.getName(), target.getName()), 6);
        //添加背景格
        gui.addAllBackGround();
        //添加交易物品格
        addTradeItemButtons();
        //添加控制按钮
        addControlButtons();
        // 给发送者打开界面
        gui.open(sender);
        // 接收者打开界面
        gui.open(target);

        // 发送提示消息
        sender.sendMessage("§a你向 §b" + target.getName() + " §a发起了交易请求");
        target.sendMessage("§b" + sender.getName() + " §a向你发起了交易请求");
    }

    //有点难处理玩家ID过长情况，先不管了
    private String createTitle(String name1, String name2) {
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
        sb.append(" ".repeat(Math.max(0, leftPadding)));

        // 玩家 1 名字
        sb.append(name1);

        // 中间空格
        sb.append(" ".repeat(middleSpace));

        // 玩家 2 名字
        sb.append(name2);

        // 右侧填充
        sb.append(" ".repeat(Math.max(0, rightPadding)));

        return sb.toString();
    }

    private void addTradeItemButtons() {
        for (int i = 0; i < gui.getRow() - 2; i++) {
            for (int j = 0; j < 9; j++) {
                switch (j) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        addTradeItem(session.getSenderPlayer(), j + i * 9);
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        addTradeItem(session.getTargetPlayer(), j + i * 9);
                }
            }
        }
    }

    //返还交易界面内玩家物品
    public void backPlayerItems(Player player) {
        List<ItemStack> itemsList = new ArrayList<>();
        if (session.isPlayerSender(player)) {
            for (int i = 0; i < gui.getRow() - 2; i++) {
                for (int j = 0; j < 9; j++) {
                    switch (j) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            ItemStack item = gui.getInventory().getItem(j + i * 9);
                            if (item != null && item.getType() != Material.AIR) {
                                itemsList.add(item);
                            }
                            break;
                    }
                }
            }
        } else {
            for (int i = 0; i < gui.getRow() - 2; i++) {
                for (int j = 0; j < 9; j++) {
                    switch (j) {
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            ItemStack item = gui.getInventory().getItem(j + i * 9);
                            if (Utils.isItemStackEmpty(item)) {
                                itemsList.add(item);
                            }
                            break;
                    }
                }
            }
        }

        if (!itemsList.isEmpty()) {
            TradeManager.addItems(player, itemsList.toArray(new ItemStack[0]));
        }
    }


    public void addTradeItem(Player player, int slot) {
        GuiButton button = new GuiButton(null);
        button.setOnClick(event -> {
            Player user = (Player) event.getWhoClicked();
            if (!player.equals(user)) {
                //TODO
                user.sendMessage("§c你只能操作自己的物品");
                user.playSound(user.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                event.setCancelled(true);
                return;
            } else {
                event.setCancelled(false);
            }
        });
        gui.addButton(slot, button);

    }


    // 添加控制按钮
    private void addControlButtons() {

        senderReadyButton.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            if (player.equals(session.getSenderPlayer())) {
                session.setSenderReady(true);
                changeButtons(senderReadyButtonYes, 48, 47, 46);
                if (session.bothReady()) {
                    prepareTrade(session);
                }
                //TODO
                player.sendMessage("§a你已确认交易，等待对方确认...");
            }
        });
        senderReadyButtonYes.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            if (player.equals(session.getSenderPlayer())) {
                session.setSenderReady(false);
                changeButtons(senderReadyButton, 48, 47, 46);
                //TODO
                player.sendMessage("§c你已取消确认交易");

            }
        });
        targetReadyButton.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            if (player.equals(session.getTargetPlayer())) {
                session.setTargetReady(true);
                changeButtons(targetReadyButtonYes, 50, 51, 52);
                if (session.bothReady()) {
                    prepareTrade(session);
                }
                // TODO
                player.sendMessage("§a你已确认交易，等待对方确认...");
            }
        });
        targetReadyButtonYes.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            if (player.equals(session.getTargetPlayer())) {
                session.setTargetReady(false);
                changeButtons(targetReadyButton, 50, 51, 52);
                //TODO
                player.sendMessage("§c你已取消确认交易");
            }
        });

        //左
        changeButtons(senderReadyButton, 48, 47, 46);
        //右
        changeButtons(targetReadyButton, 50, 51, 52);


        //添加中间提示按钮
        ItemStack infoItem = new ItemStack(Material.IRON_FENCE);
        GuiButton infoButton = new GuiButton(infoItem);
        gui.addButton(4, infoButton);
        gui.addButton(13, infoButton);
        gui.addButton(22, infoButton);
        gui.addButton(31, infoButton);
    }

    public void changeButtons(GuiButton button, int... slots) {
        for (int slot : slots) {
            gui.addButton(slot, button);
        }
    }

    //    }
    public void prepareTrade(TradeSession session) {
        //等待五秒，进行倒计时，将两边的界面改为等待按钮
        new BukkitRunnable() {
            int count = 5;

            @Override
            public void run() {
                if (count == 0) {
                    //开始交易
                    session.setConfirmed(true);
                    executeTrade(session);
                    cancel();
                } else {
                    //等待
                    session.setConfirmed(false);
                    senderReadyButtonWait.buttonItemStack.setAmount(count);
                    changeButtons(senderReadyButtonWait, 48, 47, 46);
                    targetReadyButtonWait.buttonItemStack.setAmount(count);
                    changeButtons(targetReadyButtonWait, 50, 51, 52);
                    count--;
                }
            }

        }.runTaskTimer(GoodsTrade.instance, 1L, 20L);
    }

    public void executeTrade(TradeSession session) {
        Player sender = session.getSenderPlayer();
        Player receiver = session.getTargetPlayer();
        addPlayerTradeItems(sender);
        addPlayerTradeItems(receiver);
        session.setSenderReady(false);
        sender.closeInventory();
    }

    public void addPlayerTradeItems(Player player) {
        List<ItemStack> itemsList = new ArrayList<>();
        if (session.isPlayerSender(player)) {
            for (int i = 0; i < gui.getRow() - 2; i++) {
                for (int j = 0; j < 9; j++) {
                    switch (j) {
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            ItemStack item = gui.getInventory().getItem(j + i * 9);
                            if (item != null && item.getType() != Material.AIR) {
                                itemsList.add(item);
                            }
                            break;
                    }
                }
            }
        } else {
            for (int i = 0; i < gui.getRow() - 2; i++) {
                for (int j = 0; j < 9; j++) {
                    switch (j) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            ItemStack item = gui.getInventory().getItem(j + i * 9);
                            if (Utils.isItemStackEmpty(item)) {
                                itemsList.add(item);
                            }
                            break;
                    }
                }
            }
        }

        if (!itemsList.isEmpty()) {
            TradeManager.addItems(player, itemsList.toArray(new ItemStack[0]));
        }
    }
}
