package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.hook.EconomyProvider;
import cc.sbsj.polang.goodstrade.hook.EconomyTransactionResult;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public final class EconomyTradeService {
    private EconomyTradeService() {
    }

    public static boolean isAvailable() {
        return GoodsTrade.config != null
                && GoodsTrade.config.isEconomyEnabled()
                && GoodsTrade.economyProvider != null;
    }

    public static BalanceCheck checkBalances(TradeSession session) {
        return checkBalances(
                session.getSenderPlayer(),
                session.getTargetPlayer(),
                session.getSenderMoney(),
                session.getTargetMoney()
        );
    }

    public static BalanceCheck checkBalances(Player sender, Player target,
                                             BigDecimal senderOffer, BigDecimal targetOffer) {
        MoneyTrade.PaymentPlan plan = MoneyTrade.calculate(senderOffer, targetOffer);
        if (!plan.isVaultSafe()) {
            return BalanceCheck.failure(Failure.INVALID_AMOUNT, null, BigDecimal.ZERO);
        }

        EconomyProvider provider = GoodsTrade.economyProvider;
        boolean hasMoney = plan.getSenderPayment().signum() > 0 || plan.getTargetPayment().signum() > 0;
        if (provider == null || GoodsTrade.config == null || !GoodsTrade.config.isEconomyEnabled()) {
            return hasMoney
                    ? BalanceCheck.failure(Failure.UNAVAILABLE, null, BigDecimal.ZERO)
                    : BalanceCheck.success(plan);
        }

        try {
            if (plan.getSenderPayment().signum() > 0
                    && !provider.has(sender, plan.getSenderPayment())) {
                return BalanceCheck.failure(Failure.INSUFFICIENT_BALANCE, sender, plan.getSenderPayment());
            }
            if (plan.getTargetPayment().signum() > 0
                    && !provider.has(target, plan.getTargetPayment())) {
                return BalanceCheck.failure(Failure.INSUFFICIENT_BALANCE, target, plan.getTargetPayment());
            }
        } catch (RuntimeException exception) {
            GoodsTrade.instance.getLogger().warning("Vault 余额校验失败: " + exception.getMessage());
            return BalanceCheck.failure(Failure.UNAVAILABLE, null, BigDecimal.ZERO);
        }
        return BalanceCheck.success(plan);
    }

    public static SettlementResult settle(TradeSession session) {
        BalanceCheck check = checkBalances(session);
        if (!check.isSuccess()) {
            return SettlementResult.failure(check.getFailure());
        }

        BigDecimal senderNetPayment = check.getPlan().getSenderNetPayment();
        if (senderNetPayment.signum() == 0) {
            return SettlementResult.success();
        }

        Player payer = senderNetPayment.signum() > 0 ? session.getSenderPlayer() : session.getTargetPlayer();
        Player receiver = senderNetPayment.signum() > 0 ? session.getTargetPlayer() : session.getSenderPlayer();
        BigDecimal amount = senderNetPayment.abs();
        EconomyProvider provider = GoodsTrade.economyProvider;

        EconomyTransactionResult withdrawal;
        try {
            withdrawal = provider.withdraw(payer, amount);
        } catch (RuntimeException exception) {
            return transactionFailure("withdraw", payer, amount, exception.getMessage());
        }
        if (!withdrawal.isSuccess()) {
            return transactionFailure("withdraw", payer, amount, withdrawal.getErrorMessage());
        }

        EconomyTransactionResult deposit;
        try {
            deposit = provider.deposit(receiver, amount);
        } catch (RuntimeException exception) {
            deposit = EconomyTransactionResult.failure(exception.getMessage());
        }
        if (deposit.isSuccess()) {
            return SettlementResult.success();
        }

        // No items have moved yet. Restore the withdrawal if the receiving account rejected it.
        EconomyTransactionResult refund;
        try {
            refund = provider.deposit(payer, amount);
        } catch (RuntimeException exception) {
            refund = EconomyTransactionResult.failure(exception.getMessage());
        }
        if (!refund.isSuccess()) {
            GoodsTrade.instance.getLogger().severe("Vault 交易回滚失败！玩家=" + payer.getName()
                    + ", 金额=" + amount.toPlainString() + ", 原因=" + refund.getErrorMessage());
        }
        return transactionFailure("deposit", receiver, amount, deposit.getErrorMessage());
    }

    public static String format(BigDecimal amount) {
        EconomyProvider provider = GoodsTrade.economyProvider;
        if (provider != null && MoneyTrade.isVaultAmount(amount)) {
            try {
                return provider.format(amount);
            } catch (RuntimeException ignored) {
                // Fall through to a stable provider-independent representation.
            }
        }
        BigDecimal normalized = amount.stripTrailingZeros();
        return normalized.signum() == 0 ? "0" : normalized.toPlainString();
    }

    private static SettlementResult transactionFailure(String action, Player player,
                                                       BigDecimal amount, String error) {
        GoodsTrade.instance.getLogger().warning("Vault " + action + " 失败: 玩家=" + player.getName()
                + ", 金额=" + amount.toPlainString() + ", 原因=" + error);
        return SettlementResult.failure(Failure.TRANSACTION_FAILED);
    }

    public enum Failure {
        NONE,
        INSUFFICIENT_BALANCE,
        INVALID_AMOUNT,
        UNAVAILABLE,
        TRANSACTION_FAILED
    }

    public static final class BalanceCheck {
        private final boolean success;
        private final Failure failure;
        private final Player player;
        private final BigDecimal amount;
        private final MoneyTrade.PaymentPlan plan;

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
