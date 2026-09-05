package cc.sbsj.polang.goodstrade.gui.view;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.compat.ServerCompatibility;
import cc.sbsj.polang.goodstrade.gui.Gui;
import cc.sbsj.polang.goodstrade.gui.GuiButton;
import cc.sbsj.polang.goodstrade.trade.TradeManager;
import cc.sbsj.polang.goodstrade.trade.TradeSession;
import cc.sbsj.polang.goodstrade.trade.EconomyTradeService;
import cc.sbsj.polang.goodstrade.trade.MoneyTrade;
import cc.sbsj.polang.goodstrade.hook.economy.TradeCurrency;
import cc.sbsj.polang.goodstrade.util.Utils;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
    ~~~~@~~~~
    ~~~~@~~~~
    ~~~~@~~~~
    ~~~~@~~~~
    MMMM@MMMM
    #FFF#TTT#

    ~=操作格
    #=背景物品
    @=中间提示物品
    M=当前币种金额调整按钮
    F=交易发起者的确认状态
    T=交易接受者的确认状态
*/
public class TradeView extends View {
    TradeSession session;
    public Gui gui;
    public BukkitRunnable runnable;
    Player cancelledPlayer = null;  // 记录谁取消了等待状态

    public TradeView() {
    }

    public void open(Player sender, Player target) {
        // 创建交易会话
        session = TradeManager.createSession(sender, target, this);
        initializeGui(sender, sender.getName(), target.getName());
        // 给发送者打开界面
        gui.open(sender);
        // 接收者打开界面
        gui.open(target);
    }

    public void openTest(Player administrator, String virtualPlayerName) {
        session = TradeManager.createTestSession(administrator, virtualPlayerName, this);
        initializeGui(administrator, administrator.getName(), virtualPlayerName);
        gui.open(administrator);
        administrator.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.test-opening"));
    }

    private void initializeGui(Player owner, String senderName, String targetName) {
        // 创建 GUI 界面，使用玩家名称作为标题
        gui = new Gui(owner, Utils.createTwoPlayerTitle(senderName, targetName), 6);
        //添加背景格
        gui.addAllBackGround();
        //添加交易物品格
        addTradeSlots();
        //添加控制按钮
        addControlButtons();
    }


    private void addTradeSlots() {
        for (int slot : View.senderTradeSlots) {
            setTradeItemButton(slot, true);
        }
        for (int slot : View.targetTradeSlots) {
            setTradeItemButton(slot, false);
        }
    }

    public void setTradeItemButton(int slot, boolean senderSide) {
        GuiButton button = new GuiButton(null);
        button.setOnClick(event -> {
            Player user = (Player) event.getWhoClicked();
            if (!session.canControlSide(user, senderSide)) {
                user.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.self-operation"));
                user.playSound(user.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                event.setCancelled(true);
                return;
            } else {
                event.setCancelled(false);
            }
            if (senderSide) {
                if (session.isSenderReady()) {
                    event.setCancelled(true);
                    user.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.items-locked"));
                    user.playSound(user.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                    return;
                }
            } else {
                if (session.isTargetReady()) {
                    event.setCancelled(true);
                    user.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.items-locked"));
                    user.playSound(user.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
                    return;
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
        changeButtons(infoButton, 4, 13, 22, 31, 40);
        if (session.getCurrency() != null) {
            addMoneyButtons();
            updateMoneyInfo();
        }
    }

    private void setControlButtons() {
        senderReadyButton.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            if (session.canControlSide(player, true)) {

                if (isBlackList(event, player, true)) return;
                if (!checkMoneyBeforeConfirm(player)) return;
                session.setSenderReady(true);
                changeButtons(senderReadyButtonYes, 48, 47, 46);
                if (session.bothReady()) {
                    prepareTrade(session);
                }
                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.confirm"));
            }
        });
        senderReadyButtonYes.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            if (session.canControlSide(player, true)) {
                session.setSenderReady(false);
                changeButtons(senderReadyButton, 48, 47, 46);
                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.unconfirm"));

            }
        });
        senderReadyButtonWait.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            if (session.canControlSide(player, true)) {
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
                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.cancel-ready"));
            }
        });
        targetReadyButton.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            if (session.canControlSide(player, false)) {

                if (isBlackList(event, player, false)) return;
                if (!checkMoneyBeforeConfirm(player)) return;

                session.setTargetReady(true);
                changeButtons(targetReadyButtonYes, 50, 51, 52);
                if (session.bothReady()) {
                    prepareTrade(session);
                }
                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.confirm"));
            }
        });
        targetReadyButtonYes.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            if (session.canControlSide(player, false)) {
                session.setTargetReady(false);
                changeButtons(targetReadyButton, 50, 51, 52);
                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.unconfirm"));
            }
        });

        targetReadyButtonWait.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            if (session.canControlSide(player, false)) {
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

                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.cancel-ready"));
            }
        });
        cancelReadyButton.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();

            if (session.isTestMode() && session.getSenderPlayer().equals(player)) {
                session.setSenderReady(false);
                session.setTargetReady(false);
                changeButtons(senderReadyButton, 48, 47, 46);
                changeButtons(targetReadyButton, 50, 51, 52);
                cancelledPlayer = null;
                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.trade-cancelled"));
                return;
            }

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

                player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-gui.trade-cancelled"));
            }
        });
    }

    public void changeButtons(GuiButton button, int... slots) {
        for (int slot : slots) {
            gui.addButton(slot, button);
        }
    }

    private void addMoneyButtons() {
        List<BigDecimal> amounts = session.getCurrency().getAmounts();
        int[] senderSlots = {36, 37, 38, 39};
        int[] targetSlots = {44, 43, 42, 41};
        for (int slot : senderSlots) gui.addButton(slot, new GuiButton(View.backGround.clone()));
        for (int slot : targetSlots) gui.addButton(slot, new GuiButton(View.backGround.clone()));
        for (int index = 0; index < amounts.size(); index++) {
            BigDecimal amount = amounts.get(index);
            gui.addButton(senderSlots[index], createMoneyButton(amount, index, true));
            gui.addButton(targetSlots[index], createMoneyButton(amount, index, false));
        }
    }

    private GuiButton createMoneyButton(BigDecimal step, int index, boolean senderSide) {
        ItemStack item = View.moneyButtonItems.get(index).clone();
        replaceAmountPlaceholder(item, EconomyTradeService.format(session.getCurrency(), step));
        GuiButton button = new GuiButton(item);
        button.setOnClick(event -> handleMoneyClick((Player) event.getWhoClicked(), step,
                event.isLeftClick(), event.isRightClick(), senderSide));
        return button;
    }

    private void handleMoneyClick(Player player, BigDecimal step,
                                  boolean leftClick, boolean rightClick, boolean senderSide) {
        if (!session.canControlSide(player, senderSide)) {
            rejectMoneyChange(player, "trade-gui.self-operation");
            return;
        }
        if (!leftClick && !rightClick) return;
        if ((senderSide && session.isSenderReady())
                || (!senderSide && session.isTargetReady())) {
            rejectMoneyChange(player, "trade-gui.money-locked");
            return;
        }

        BigDecimal current = senderSide ? session.getSenderMoney() : session.getTargetMoney();
        BigDecimal adjusted = MoneyTrade.adjust(
                current,
                step,
                leftClick,
                GoodsTrade.config.isNegativeEconomyAllowed()
        );
        if (adjusted.compareTo(current) == 0) {
            rejectMoneyChange(player, "trade-gui.money-minimum");
            return;
        }

        BigDecimal senderOffer = senderSide ? adjusted : session.getSenderMoney();
        BigDecimal targetOffer = senderSide ? session.getTargetMoney() : adjusted;
        if (session.isTestMode()) {
            MoneyTrade.PaymentPlan plan = MoneyTrade.calculate(senderOffer, targetOffer);
            if (!plan.isFinite() || !session.getCurrency().getProvider().supports(plan.getSenderPayment())
                    || !session.getCurrency().getProvider().supports(plan.getTargetPayment())) {
                rejectMoneyChange(player, "trade-gui.money-invalid");
                return;
            }
        } else {
            EconomyTradeService.BalanceCheck check = EconomyTradeService.checkBalances(
                    session.getCurrency(), session.getSenderPlayer(), session.getTargetPlayer(), senderOffer, targetOffer);
            if (!check.isSuccess()) {
                sendMoneyCheckFailure(player, check);
                return;
            }
        }

        if (senderSide) {
            session.setSenderMoney(adjusted);
        } else {
            session.setTargetMoney(adjusted);
        }
        resetConfirmationsAfterOfferChange(player, senderSide, "trade-gui.money-offer-changed");
        updateMoneyInfo();
        String changed = GoodsTrade.lang.replacePlaceholders(
                GoodsTrade.lang.getString("trade-gui.money-changed"),
                "%amount%", EconomyTradeService.format(session.getCurrency(), adjusted)
        );
        player.sendMessage(GoodsTrade.getPrefix() + changed);
        player.playSound(player.getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.get(), 1.0f, 1.2f);
    }

    public void onItemOfferChange(Player changer) {
        resetConfirmationsAfterOfferChange(changer, session.isPlayerSender(changer), "trade-gui.items-offer-changed");
    }

    private void resetConfirmationsAfterOfferChange(Player changer, boolean senderSide, String messageKey) {
        if (!session.isSenderReady() && !session.isTargetReady() && cancelledPlayer == null) return;
        if (runnable != null) {
            try {
                runnable.cancel();
            } catch (IllegalStateException ignored) {
                // It was created but not scheduled, or has already stopped.
            }
            runnable = null;
        }
        session.setSenderReady(false);
        session.setTargetReady(false);
        session.setConfirmed(false);
        cancelledPlayer = null;
        changeButtons(senderReadyButton, 48, 47, 46);
        changeButtons(targetReadyButton, 50, 51, 52);

        String message = GoodsTrade.lang.replacePlaceholders(
                GoodsTrade.lang.getString(messageKey),
                "%player%", senderSide ? session.getSenderDisplayName() : session.getTargetDisplayName()
        );
        Player other = session.isTestMode() ? changer : TradeManager.getOtherPlayer(changer, session);
        other.sendMessage(GoodsTrade.getPrefix() + message);
        other.playSound(other.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
    }

    private boolean checkMoneyBeforeConfirm(Player player) {
        if (session.isTestMode()) return true;
        EconomyTradeService.BalanceCheck check = EconomyTradeService.checkBalances(session);
        if (check.isSuccess()) return true;
        sendMoneyCheckFailure(player, check);
        return false;
    }

    private void sendMoneyCheckFailure(Player player, EconomyTradeService.BalanceCheck check) {
        if (check.getFailure() == EconomyTradeService.Failure.INSUFFICIENT_BALANCE
                && check.getPlayer() != null) {
            String message = GoodsTrade.lang.replacePlaceholders(
                    GoodsTrade.lang.getString("trade-gui.money-insufficient"),
                    "%player%", check.getPlayer().getName(),
                    "%amount%", EconomyTradeService.format(check.getCurrency(), check.getAmount())
            );
            player.sendMessage(GoodsTrade.getPrefix() + message);
        } else if (check.getFailure() == EconomyTradeService.Failure.INVALID_AMOUNT) {
            rejectMoneyChange(player, "trade-gui.money-invalid");
            return;
        } else {
            rejectMoneyChange(player, "trade-gui.money-unavailable");
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
    }

    private void rejectMoneyChange(Player player, String path) {
        player.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString(path));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0f, 1.0f);
    }

    private void updateMoneyInfo() {
        ItemStack item = View.infoItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add("");
        if (session.isTestMode()) {
            lore.add(GoodsTrade.lang.getString("trade-view.test-mode"));
        }
        lore.add(GoodsTrade.lang.getString("trade-view.currency-selected").replace("%currency%", currencyName()));
        if (session.getCurrencies().size() > 1) lore.add(GoodsTrade.lang.getString("trade-view.currency-switch"));
        for (TradeCurrency currency : session.getCurrencies()) {
            MoneyTrade.PaymentPlan plan = MoneyTrade.calculate(session.getOffer(currency, true), session.getOffer(currency, false));
            if (currency != session.getCurrency() && plan.getSenderPayment().signum() == 0
                    && plan.getTargetPayment().signum() == 0) continue;
            lore.add(paymentLine("trade-view.sender-payment", session.getSenderDisplayName(), currency, plan.getSenderPayment()));
            lore.add(paymentLine("trade-view.target-payment", session.getTargetDisplayName(), currency, plan.getTargetPayment()));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        infoButton.buttonItemStack = item;
        infoButton.setOnClick(event -> {
            if (!event.isLeftClick() || session.getCurrencies().size() < 2) return;
            Player player = (Player) event.getWhoClicked();
            if (!session.canControlSide(player, true) && !session.canControlSide(player, false)) return;
            if (session.isSenderReady() || session.isTargetReady()) {
                rejectMoneyChange(player, "trade-gui.currency-locked");
                return;
            }
            session.nextCurrency();
            addMoneyButtons();
            updateMoneyInfo();
        });
        changeButtons(infoButton, 4, 13, 22, 31, 40);
    }

    private String paymentLine(String path, String playerName, TradeCurrency currency, BigDecimal amount) {
        return GoodsTrade.lang.replacePlaceholders(
                GoodsTrade.lang.getString(path),
                "%player%", playerName,
                "%amount%", EconomyTradeService.format(currency, amount)
        );
    }

    private String currencyName() {
        TradeCurrency currency = session.getCurrency();
        return currency.getName().isEmpty() ? currency.getProvider().getName() : currency.getName();
    }

    private void replaceAmountPlaceholder(ItemStack item, String amount) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (meta.hasDisplayName()) {
            meta.setDisplayName(meta.getDisplayName().replace("%amount%", amount).replace("%currency%", currencyName()));
        }
        if (meta.hasLore()) {
            List<String> lore = new ArrayList<>();
            for (String line : meta.getLore()) {
                lore.add(line.replace("%amount%", amount).replace("%currency%", currencyName()));
            }
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    public void prepareTrade(TradeSession session) {
        //等待五秒，进行倒计时，将两边的界面改为等待按钮
        runnable = new BukkitRunnable() {
            int count = GoodsTrade.config.getWaitTime();

            @Override
            public void run() {
                if (count == 0) {
                    // 防止交易已被取消后仍然执行
                    if (!TradeManager.isTrade(session.getSenderPlayer()) || !session.bothReady()) {
                        cancel();
                        return;
                    }
                    executeTrade(session);
                    cancel();
                } else {
                    //等待
                    session.setConfirmed(false);
                    senderReadyButtonWait.buttonItemStack.setAmount(count);
                    changeButtons(senderReadyButtonWait, 48, 47, 46);
                    session.getSenderPlayer().playSound(session.getSenderPlayer().getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.get(), 1.0f, 1.0f);

                    targetReadyButtonWait.buttonItemStack.setAmount(count);
                    changeButtons(targetReadyButtonWait, 50, 51, 52);
                    if (!session.isTestMode()) {
                        session.getTargetPlayer().playSound(session.getTargetPlayer().getLocation(), XSound.BLOCK_NOTE_BLOCK_PLING.get(), 1.0f, 1.0f);
                    }

                    count--;

                }
            }

        };
        runnable.runTaskTimer(GoodsTrade.instance, 1L, 20L);
    }

    public void executeTrade(TradeSession session) {
        Player sender = session.getSenderPlayer();
        Player receiver = session.getTargetPlayer();

        if (session.isTestMode()) {
            completeTestTrade(sender);
            return;
        }

        // 所有币种先校验余额，再逐个结算；失败时撤回先前币种。
        // Items do not move if the balance changed or the provider rejects the transaction.
        EconomyTradeService.SettlementResult settlement = EconomyTradeService.settle(session);
        if (!settlement.isSuccess()) {
            abortFailedMoneyTrade(settlement.getFailure());
            return;
        }

        session.setConfirmed(true);

        // 交换交易物品
        addPlayerTradeItems(sender);
        addPlayerTradeItems(receiver);

        // 重置交易状态
        session.setSenderReady(false);
        session.setTargetReady(false);
        session.setConfirmed(true);

        // 交易成功提示（在关闭界面前发送，确保玩家能看到）
        sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.success"));
        receiver.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.success"));
        sender.playSound(sender.getLocation(), XSound.ENTITY_PLAYER_LEVELUP.get(), 1.0f, 1.5f);
        receiver.playSound(receiver.getLocation(), XSound.ENTITY_PLAYER_LEVELUP.get(), 1.0f, 1.5f);

        // 先移除会话，再关闭界面，防止 onInventoryClose 重复处理
        TradeManager.removeSession(sender);

        // 手动处理光标物品后清空光标，防止 Bukkit closeInventory 内部自动返还导致重复
        returnCursorItem(sender);
        // 统一走兼容层：1.12 没有 closeInventory(Reason)。
        ServerCompatibility.closeInventory(sender);
        returnCursorItem(receiver);
        // 统一走兼容层：1.12 没有 closeInventory(Reason)。
        ServerCompatibility.closeInventory(receiver);
    }

    private void completeTestTrade(Player administrator) {
        backPlayerItems(administrator);
        session.setSenderReady(false);
        session.setTargetReady(false);
        session.setConfirmed(true);
        administrator.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString("trade-status.test-success"));
        administrator.playSound(administrator.getLocation(), XSound.ENTITY_PLAYER_LEVELUP.get(), 1.0f, 1.5f);
        TradeManager.removeSession(administrator);
        returnCursorItem(administrator);
        ServerCompatibility.closeInventory(administrator);
    }

    private void abortFailedMoneyTrade(EconomyTradeService.Failure failure) {
        Player sender = session.getSenderPlayer();
        Player target = session.getTargetPlayer();
        session.setSenderReady(false);
        session.setTargetReady(false);
        session.setConfirmed(false);
        backPlayerItems(sender);
        backPlayerItems(target);

        String path = failure == EconomyTradeService.Failure.ROLLBACK_FAILED
                ? "trade-status.money-rollback-failed"
                : failure == EconomyTradeService.Failure.INSUFFICIENT_BALANCE
                ? "trade-status.money-balance-changed"
                : "trade-status.money-settlement-failed";
        sender.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString(path));
        target.sendMessage(GoodsTrade.getPrefix() + GoodsTrade.lang.getString(path));
        TradeManager.removeSession(sender);
        returnCursorItem(sender);
        ServerCompatibility.closeInventory(sender);
        returnCursorItem(target);
        ServerCompatibility.closeInventory(target);
    }

    /**
     * 手动将光标物品放入玩家背包并清空光标，防止 closeInventory 内部重复返还
     */
    private void returnCursorItem(Player player) {
        ItemStack cursor = player.getOpenInventory().getCursor();
        if (Utils.isItemStackNotEmpty(cursor)) {
            Utils.addItems(player, cursor);
        }
        player.setItemOnCursor(null);
    }

    //给玩家Gui上的物品
    public void addPlayerTradeItems(Player player) {
        List<ItemStack> itemsList = new ArrayList<>();
        if (session.isPlayerSender(player)) {
            for (int slot : View.targetTradeSlots) {
                ItemStack item = gui.getInventory().getItem(slot);
                if (Utils.isItemStackNotEmpty(item)) {
                    itemsList.add(item);
                    gui.getInventory().setItem(slot, air);
                }
            }
        } else {
            for (int slot : View.senderTradeSlots) {
                ItemStack item = gui.getInventory().getItem(slot);
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
        List<ItemStack> itemsList = new ArrayList<>();
        if (session.isTestMode()) {
            collectTradeItems(itemsList, View.senderTradeSlots);
            collectTradeItems(itemsList, View.targetTradeSlots);
            if (!itemsList.isEmpty()) {
                Utils.addItems(player, itemsList.toArray(new ItemStack[0]));
            }
            return;
        }
        if (session.isPlayerSender(player)) {
            for (int slot : View.senderTradeSlots) {
                ItemStack item = gui.getInventory().getItem(slot);
                if (Utils.isItemStackNotEmpty(item)) {
                    itemsList.add(item);
                    //防止异常，清掉物品
                    gui.getInventory().setItem(slot, air);
                }
            }
        } else {
            for (int slot : View.targetTradeSlots) {
                ItemStack item = gui.getInventory().getItem(slot);
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

    private void collectTradeItems(List<ItemStack> itemsList, List<Integer> slots) {
        for (int slot : slots) {
            ItemStack item = gui.getInventory().getItem(slot);
            if (Utils.isItemStackNotEmpty(item)) {
                itemsList.add(item);
                gui.getInventory().setItem(slot, air);
            }
        }
    }
}
