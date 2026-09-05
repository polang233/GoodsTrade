package cc.sbsj.polang.goodstrade.trade;

import java.math.BigDecimal;

/**
 * Pure offer calculations. Providers may use doubles, while offers are kept as decimals
 * so repeated GUI clicks do not accumulate floating-point drift.
 */
public final class MoneyTrade {
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private MoneyTrade() {
    }

    public static BigDecimal adjust(BigDecimal current, BigDecimal step, boolean increase, boolean allowNegative) {
        BigDecimal adjusted = increase ? current.add(step) : current.subtract(step);
        if (!allowNegative && adjusted.signum() < 0) {
            return ZERO;
        }
        return adjusted.stripTrailingZeros();
    }

    public static PaymentPlan calculate(BigDecimal senderOffer, BigDecimal targetOffer) {
        BigDecimal senderPayment = positive(senderOffer).add(positive(targetOffer.negate()));
        BigDecimal targetPayment = positive(targetOffer).add(positive(senderOffer.negate()));
        return new PaymentPlan(senderPayment, targetPayment);
    }

    public static boolean isFiniteAmount(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) return false;
        if (amount.signum() == 0) return true;
        double value = amount.doubleValue();
        return value > 0 && !Double.isInfinite(value) && !Double.isNaN(value);
    }

    private static BigDecimal positive(BigDecimal value) {
        return value.signum() > 0 ? value : ZERO;
    }

    public static final class PaymentPlan {
        private final BigDecimal senderPayment;
        private final BigDecimal targetPayment;

        private PaymentPlan(BigDecimal senderPayment, BigDecimal targetPayment) {
            this.senderPayment = senderPayment.stripTrailingZeros();
            this.targetPayment = targetPayment.stripTrailingZeros();
        }

        public BigDecimal getSenderPayment() {
            return senderPayment;
        }

        public BigDecimal getTargetPayment() {
            return targetPayment;
        }

        /** Positive means sender pays target; negative means target pays sender. */
        public BigDecimal getSenderNetPayment() {
            return senderPayment.subtract(targetPayment).stripTrailingZeros();
        }

        public boolean isFinite() {
            return MoneyTrade.isFiniteAmount(senderPayment) && MoneyTrade.isFiniteAmount(targetPayment);
        }
    }
}
