package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.hook.economy.EconomyProvider;
import cc.sbsj.polang.goodstrade.hook.economy.EconomyTransactionResult;
import cc.sbsj.polang.goodstrade.hook.economy.TradeCurrency;
import org.bukkit.entity.Player;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class EconomyTradeService {
    private EconomyTradeService() { }

    private static java.util.logging.Logger logger() {
        return GoodsTrade.instance == null ? java.util.logging.Logger.getLogger("GoodsTrade")
                : GoodsTrade.instance.getLogger();
    }

    public static BalanceCheck checkBalances(TradeSession session) {
        for (TradeCurrency currency : session.getCurrencies()) {
            BalanceCheck check = checkBalances(currency, session.getSenderPlayer(), session.getTargetPlayer(),
                    session.getOffer(currency, true), session.getOffer(currency, false));
            if (!check.isSuccess()) return check;
        }
        return BalanceCheck.success(MoneyTrade.calculate(BigDecimal.ZERO, BigDecimal.ZERO));
    }

    public static BalanceCheck checkBalances(TradeCurrency currency, Player sender, Player target,
                                             BigDecimal senderOffer, BigDecimal targetOffer) {
        BalanceCheck check = checkCurrency(currency, sender, target, senderOffer, targetOffer);
        check.currency = currency;
        return check;
    }

    private static BalanceCheck checkCurrency(TradeCurrency currency, Player sender, Player target,
                                              BigDecimal senderOffer, BigDecimal targetOffer) {
        MoneyTrade.PaymentPlan plan = MoneyTrade.calculate(senderOffer, targetOffer);
        if (!plan.isFinite()) return BalanceCheck.failure(Failure.INVALID_AMOUNT, null, BigDecimal.ZERO);
        if (plan.getSenderPayment().signum() == 0 && plan.getTargetPayment().signum() == 0) {
            return BalanceCheck.success(plan);
        }
        if (currency == null) return BalanceCheck.failure(Failure.UNAVAILABLE, null, BigDecimal.ZERO);
        EconomyProvider provider = currency.getProvider();
        try {
            if (!provider.isAvailable()) return BalanceCheck.failure(Failure.UNAVAILABLE, null, BigDecimal.ZERO);
            if (!provider.supports(plan.getSenderPayment()) || !provider.supports(plan.getTargetPayment())
                    || !provider.supports(plan.getSenderNetPayment().abs())) {
                return BalanceCheck.failure(Failure.INVALID_AMOUNT, null, BigDecimal.ZERO);
            }
            if (plan.getSenderPayment().signum() > 0 && !provider.has(sender, plan.getSenderPayment())) {
                return BalanceCheck.failure(Failure.INSUFFICIENT_BALANCE, sender, plan.getSenderPayment());
            }
            if (plan.getTargetPayment().signum() > 0 && !provider.has(target, plan.getTargetPayment())) {
                return BalanceCheck.failure(Failure.INSUFFICIENT_BALANCE, target, plan.getTargetPayment());
            }
        } catch (RuntimeException | LinkageError exception) {
            logger().warning("货币 " + currency.getId() + " 余额校验失败: " + exception);
            return BalanceCheck.failure(Failure.UNAVAILABLE, null, BigDecimal.ZERO);
        }
        return BalanceCheck.success(plan);
    }

    public static SettlementResult settle(TradeSession session) {
        if (session.isTestMode()) return SettlementResult.success();
        BalanceCheck check = checkBalances(session);
        if (!check.isSuccess()) return SettlementResult.failure(check.getFailure());
        List<Transfer> completed = new ArrayList<>();
        for (TradeCurrency currency : session.getCurrencies()) {
            BigDecimal net = MoneyTrade.calculate(session.getOffer(currency, true),
                    session.getOffer(currency, false)).getSenderNetPayment();
            if (net.signum() == 0) continue;
            Transfer transfer = new Transfer(currency,
                    net.signum() > 0 ? session.getSenderPlayer() : session.getTargetPlayer(),
                    net.signum() > 0 ? session.getTargetPlayer() : session.getSenderPlayer(), net.abs());
            SettlementResult result = transfer.perform();
            if (!result.isSuccess()) {
                boolean rollbackFailed = result.getFailure() == Failure.ROLLBACK_FAILED;
                // 后续币种失败时，按反序撤回已完成的转账。
                for (int index = completed.size() - 1; index >= 0; index--) {
                    Transfer previous = completed.get(index);
                    if (!previous.reverse().perform().isSuccess()) {
                        rollbackFailed = true;
                        previous.log("跨币种回滚失败，需要人工核对");
                    }
                }
                return SettlementResult.failure(rollbackFailed ? Failure.ROLLBACK_FAILED : result.getFailure());
            }
            completed.add(transfer);
        }
        return SettlementResult.success();
    }

    private static final class Transfer {
        private final TradeCurrency currency;
        private final Player payer;
        private final Player receiver;
        private final BigDecimal amount;
        private Transfer(TradeCurrency currency, Player payer, Player receiver, BigDecimal amount) {
            this.currency = currency;
            this.payer = payer;
            this.receiver = receiver;
            this.amount = amount;
        }
        private Transfer reverse() { return new Transfer(currency, receiver, payer, amount); }
        private EconomyTransactionResult change(Player player, boolean deposit) {
            try {
                EconomyProvider provider = currency.getProvider();
                if (!provider.isAvailable()) return EconomyTransactionResult.failure("提供者已停用");
                EconomyTransactionResult result = deposit ? provider.deposit(player, amount) : provider.withdraw(player, amount);
                return result == null ? EconomyTransactionResult.failure("空响应") : result;
            } catch (RuntimeException | LinkageError exception) {
                return EconomyTransactionResult.failure(exception.toString());
            }
        }
        private SettlementResult perform() {
            EconomyTransactionResult withdrawal = change(payer, false);
            if (!withdrawal.isSuccess()) {
                log("扣款失败: " + withdrawal.getErrorMessage());
                return SettlementResult.failure(Failure.TRANSACTION_FAILED);
            }
            EconomyTransactionResult deposit = change(receiver, true);
            if (deposit.isSuccess()) return SettlementResult.success();
            log("入账失败: " + deposit.getErrorMessage());
            EconomyTransactionResult refund = change(payer, true);
            if (!refund.isSuccess()) {
                log("退款失败，需要人工核对: " + refund.getErrorMessage());
                return SettlementResult.failure(Failure.ROLLBACK_FAILED);
            }
            return SettlementResult.failure(Failure.TRANSACTION_FAILED);
        }
        private void log(String message) {
            logger().severe("货币=" + currency.getId() + ", 付款=" + payer.getUniqueId()
                    + ", 收款=" + receiver.getUniqueId() + ", 金额=" + amount.toPlainString() + ", " + message);
        }
    }

    public static String format(TradeCurrency currency, BigDecimal amount) {
        if (currency != null) {
            if (!currency.getName().isEmpty()) return currency.format(amount);
            try { return currency.getProvider().format(amount); }
            catch (RuntimeException | LinkageError ignored) { }
        }
        return amount.stripTrailingZeros().toPlainString();
    }

    public enum Failure {
        NONE,
        INSUFFICIENT_BALANCE,
        INVALID_AMOUNT,
        UNAVAILABLE,
        TRANSACTION_FAILED,
        ROLLBACK_FAILED
    }

    public static final class BalanceCheck {
        private final boolean success;
        private final Failure failure;
        private final Player player;
        private final BigDecimal amount;
        private final MoneyTrade.PaymentPlan plan;
        private TradeCurrency currency;

        public TradeCurrency getCurrency() { return currency; }

        private BalanceCheck(boolean success, Failure failure, Player player,
                             BigDecimal amount, MoneyTrade.PaymentPlan plan) {
            this.success = success;
            this.failure = failure;
            this.player = player;
            this.amount = amount;
            this.plan = plan;
        }

        private static BalanceCheck success(MoneyTrade.PaymentPlan plan) {
            return new BalanceCheck(true, Failure.NONE, null, BigDecimal.ZERO, plan);
        }

        private static BalanceCheck failure(Failure failure, Player player, BigDecimal amount) {
            return new BalanceCheck(false, failure, player, amount, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public Failure getFailure() {
            return failure;
        }

        public Player getPlayer() {
            return player;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public MoneyTrade.PaymentPlan getPlan() {
            return plan;
        }
    }

    public static final class SettlementResult {
        private final boolean success;
        private final Failure failure;

        private SettlementResult(boolean success, Failure failure) {
            this.success = success;
            this.failure = failure;
        }

        private static SettlementResult success() {
            return new SettlementResult(true, Failure.NONE);
        }

        private static SettlementResult failure(Failure failure) {
            return new SettlementResult(false, failure);
        }

        public boolean isSuccess() {
            return success;
        }

        public Failure getFailure() {
            return failure;
        }
    }
}
