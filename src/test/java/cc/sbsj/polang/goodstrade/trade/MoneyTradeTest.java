package cc.sbsj.polang.goodstrade.trade;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoneyTradeTest {
    @Test
    public void positiveOffersRemainSeparatePaymentObligations() {
        MoneyTrade.PaymentPlan plan = MoneyTrade.calculate(amount("10000"), amount("5000"));

        assertAmount("10000", plan.getSenderPayment());
        assertAmount("5000", plan.getTargetPayment());
        assertAmount("5000", plan.getSenderNetPayment());
    }

    @Test
    public void negativeSenderOfferMakesTargetPay() {
        MoneyTrade.PaymentPlan plan = MoneyTrade.calculate(amount("-10000"), BigDecimal.ZERO);

        assertAmount("0", plan.getSenderPayment());
        assertAmount("10000", plan.getTargetPayment());
        assertAmount("-10000", plan.getSenderNetPayment());
    }

    @Test
    public void negativeOfferAddsToOtherPlayersOwnPayment() {
        MoneyTrade.PaymentPlan plan = MoneyTrade.calculate(amount("-10000"), amount("5000"));

        assertAmount("0", plan.getSenderPayment());
        assertAmount("15000", plan.getTargetPayment());
    }

    @Test
    public void disabledNegativeOffersClampAtZero() {
        BigDecimal adjusted = MoneyTrade.adjust(amount("500"), amount("1000"), false, false);

        assertAmount("0", adjusted);
    }

    @Test
    public void enabledNegativeOffersCanCrossZero() {
        BigDecimal adjusted = MoneyTrade.adjust(amount("500"), amount("1000"), false, true);

        assertAmount("-500", adjusted);
    }

    @Test
    public void vaultRangeRejectsOverflowButAcceptsNormalValues() {
        assertTrue(MoneyTrade.isFiniteAmount(amount("10000.25")));
        assertTrue(MoneyTrade.isFiniteAmount(BigDecimal.ZERO));
        assertFalse(MoneyTrade.isFiniteAmount(amount("1e10000")));
        assertFalse(MoneyTrade.isFiniteAmount(amount("1e-10000")));
        assertFalse(MoneyTrade.isFiniteAmount(amount("-1")));
    }

    private static BigDecimal amount(String value) {
        return new BigDecimal(value);
    }

    private static void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, amount(expected).compareTo(actual));
    }
}
