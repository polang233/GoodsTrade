package cc.sbsj.polang.goodstrade.gui.view;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.gui.Gui;
import cc.sbsj.polang.goodstrade.gui.GuiButton;
import cc.sbsj.polang.goodstrade.manager.ConfigManager;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import cc.sbsj.polang.goodstrade.trade.TradeSession;
import cc.sbsj.polang.goodstrade.util.Utils;
import com.cryptomorin.xseries.XSound;
import lombok.val;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

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
public class TradeView extends View {
    public Gui gui;
    public BukkitRunnable runnable;
    TradeSession session;
    Player cancelledPlayer = null;  // 记录谁取消了等待状态

    public TradeView() {
    }

    public void open(Player sender, Player target) {
        // 创建交易会话
        session = TradeManager.createSession(sender, target, this);
        // 创建 GUI 界面
        gui = new Gui(sender, Utils.createTwoPlayerTitle(sender.getName(), target.getName()), 6);
        //添加背景格
        gui.addAllBackGround();
        //添加交易物品格
        addTradeSlots();
        //添加控制按钮
        addControlButtons();
        // 给发送者打开界面
        gui.open(sender);
        // 接收者打开界面
        gui.open(target);
    }


    private void addTradeSlots() {
        for (val slot : View.senderTradeSlots) {
            setTradeItemButton(session.getSenderPlayer(), slot);
        }
        for (val slot : View.targetTradeSlots) {
            setTradeItemButton(session.getTargetPlayer(), slot);
        }
    }

    public void setTradeItemButton(Player player, int slot) {
        val button = new GuiButton(null);
        button.setOnClick(event -> {
            val user = (Player) event.getWhoClicked();
            if (!player.equals(user)) {
                user.sendMessage("§c你只能操作自己的物品");
                user.playSound(user.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                event.setCancelled(true);
                return;
            } else {
                event.setCancelled(false);
            }
            if (session.isPlayerSender(user)) {
                if (session.isSenderReady()) {
                    event.setCancelled(true);
                    user.sendMessage("§c你已确认，物品状态锁定！");
                    user.playSound(user.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                }
            } else {
                if (session.isTargetReady()) {
                    event.setCancelled(true);
                    user.sendMessage("§c你已确认，物品状态锁定！");
                    user.playSound(user.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                }
            }

        });
        gui.addButton(slot, button);

    }


    // 添加控制按钮
    private void addControlButtons() {
        //添加控制按钮
        setControlButtons();
        //左
        changeButtons(senderReadyButton, 48, 47, 46);
        //右
        changeButtons(targetReadyButton, 50, 51, 52);
        //添加中间提示按钮
        changeButtons(infoButton, 4, 13, 22, 31);
    }

    private void setControlButtons() {
        senderReadyButton.setOnClick(event -> {
            val player = (Player) event.getWhoClicked();
            if (player.equals(session.getSenderPlayer())) {
                if (isBlackList(event, player)) return;
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
            val player = (Player) event.getWhoClicked();
            if (player.equals(session.getSenderPlayer())) {
                session.setSenderReady(false);
                changeButtons(senderReadyButton, 48, 47, 46);
                //TODO
                player.sendMessage("§c你已取消确认交易");

            }
        });
        senderReadyButtonWait.setOnClick(event -> {
            val player = (Player) event.getWhoClicked();
            if (player.equals(session.getSenderPlayer())) {
                runnable.cancel();
                //设置为对方取消
                session.setTargetReady(false);
                session.setSenderReady(false);
                //设置自己的按钮
                changeButtons(senderReadyButton, 48, 47, 46);

                //设置对方按钮
                changeButtons(cancelReadyButton, 50, 51, 52);

                //记录是发送者取消的
                cancelledPlayer = session.getTargetPlayer();
                player.sendMessage("§c你已取消确认状态");
            }
        });
        targetReadyButton.setOnClick(event -> {
            val player = (Player) event.getWhoClicked();
            if (player.equals(session.getTargetPlayer())) {

                if (isBlackList(event, player)) return;

                session.setTargetReady(true);
                changeButtons(targetReadyButtonYes, 50, 51, 52);
                if (session.bothReady()) {
                    prepareTrade(session);
                }
                player.sendMessage("§a你已确认交易，等待对方确认...");
            }
        });
        targetReadyButtonYes.setOnClick(event -> {
            val player = (Player) event.getWhoClicked();
            if (player.equals(session.getTargetPlayer())) {
                session.setTargetReady(false);
                changeButtons(targetReadyButton, 50, 51, 52);
                player.sendMessage("§c你已取消确认交易");
            }
        });

        targetReadyButtonWait.setOnClick(event -> {
            val player = (Player) event.getWhoClicked();
            if (player.equals(session.getTargetPlayer())) {
                runnable.cancel();
                //设置为对方取消
                session.setSenderReady(false);
                session.setTargetReady(false);

                //设置自己的按钮
                changeButtons(targetReadyButton, 50, 51, 52);

                //设置对方按钮
                changeButtons(cancelReadyButton, 48, 47, 46);

                //记录是接受者取消的
                cancelledPlayer = session.getSenderPlayer();

                player.sendMessage("§c你已取消确认状态");
            }
        });
        cancelReadyButton.setOnClick(event -> {
            val player = (Player) event.getWhoClicked();

            //只有被取消的那一方才能点击这个按钮
            if (player.equals(cancelledPlayer)) {
                if (player == session.getSenderPlayer()) {
                    session.setSenderReady(false);
                    changeButtons(senderReadyButton, 48, 47, 46);
                } else {
                    session.setTargetReady(false);
                    changeButtons(targetReadyButton, 50, 51, 52);
                }
                //清除记录
                cancelledPlayer = null;

                player.sendMessage("§c交易已取消，回到初始状态");
            }
        });
    }

    public void changeButtons(GuiButton button, int... slots) {
        for (val slot : slots) {
            gui.addButton(slot, button);
        }
    }

    public void prepareTrade(TradeSession session) {
        //等待五秒，进行倒计时，将两边的界面改为等待按钮
        runnable = new BukkitRunnable() {
            int count = ConfigManager.getWaitTime();

            @Override
            public void run() {
                if (count == 0) {
                    //开始交易
                    session.setConfirmed(true);
                    executeTrade(session);

                    session.getSenderPlayer().playSound(session.getSenderPlayer().getLocation(), XSound.ENTITY_PLAYER_LEVELUP.get(), 1.0f, 1.5f);
                    session.getTargetPlayer().playSound(session.getTargetPlayer().getLocation(), XSound.ENTITY_PLAYER_LEVELUP.get(), 1.0f, 1.5f);

                    cancel();
                } else {
                    //等待
                    session.setConfirmed(false);
                    senderReadyButtonWait.buttonItemStack.setAmount(count);
                    changeButtons(senderReadyButtonWait, 48, 47, 46);
                    session.getSenderPlayer().playSound(session.getSenderPlayer().getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.get(), 1.0f, 1.0f);

                    targetReadyButtonWait.buttonItemStack.setAmount(count);
                    changeButtons(targetReadyButtonWait, 50, 51, 52);
                    session.getTargetPlayer().playSound(session.getTargetPlayer().getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.get(), 1.0f, 1.0f);

                    count--;

                }
            }

        };
        runnable.runTaskTimer(GoodsTrade.getInstance(), 1L, 20L);
    }

    public void executeTrade(TradeSession session) {
        val sender = session.getSenderPlayer();
        val receiver = session.getTargetPlayer();
        addPlayerTradeItems(sender);
        addPlayerTradeItems(receiver);
        session.setSenderReady(false);
        sender.closeInventory();
    }

    //给玩家Gui上的物品
    public void addPlayerTradeItems(Player player) {
        val itemsList = new ArrayList<ItemStack>();
        if (session.isPlayerSender(player)) {
            for (val slot : View.targetTradeSlots) {
                val item = gui.getInventory().getItem(slot);
                if (Utils.isItemStackNotEmpty(item)) {
                    itemsList.add(item);
                    gui.getInventory().setItem(slot, air);
                }
            }
        } else {
            for (val slot : View.senderTradeSlots) {
                val item = gui.getInventory().getItem(slot);
                if (Utils.isItemStackNotEmpty(item)) {
                    itemsList.add(item);
                    gui.getInventory().setItem(slot, air);
                }
            }
        }
        //如果不为空就放到他背包
        if (!itemsList.isEmpty()) {
            Utils.addItems(player, itemsList.toArray(new ItemStack[0]));
        }
    }

    //返还交易界面内玩家物品
    public void backPlayerItems(Player player) {
        val itemsList = new ArrayList<ItemStack>();
        if (session.isPlayerSender(player)) {
            for (val slot : View.senderTradeSlots) {
                val item = gui.getInventory().getItem(slot);
                if (Utils.isItemStackNotEmpty(item)) {
                    itemsList.add(item);
                    //防止异常，清掉物品
                    gui.getInventory().setItem(slot, air);
                }
            }
        } else {
            for (val slot : View.targetTradeSlots) {
                val item = gui.getInventory().getItem(slot);
                if (Utils.isItemStackNotEmpty(item)) {
                    itemsList.add(item);
                    //防止异常，清掉物品
                    gui.getInventory().setItem(slot, air);
                }
            }
        }

        if (!itemsList.isEmpty()) {
            Utils.addItems(player, itemsList.toArray(new ItemStack[0]));
        }
    }
}
