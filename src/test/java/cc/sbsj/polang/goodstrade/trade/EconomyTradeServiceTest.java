package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.hook.economy.*;
import cc.sbsj.polang.goodstrade.hook.economy.provider.ExperienceEconomyProvider;
import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.Assert.*;

public class EconomyTradeServiceTest {
    private final Player sender = player("sender");
    private final Player target = player("target");

    @After public void reset() { GoodsTrade.currencies = Collections.emptyList(); }

    private static Player player(String name) {
        UUID id = UUID.nameUUIDFromBytes(name.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        int[] level = {30};
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class}, (p, m, a) -> {
            switch (m.getName()) {
                case "getUniqueId": return id;
                case "getLevel": return level[0];
                case "setLevel": level[0] = (int) a[0]; return null;
                case "getExp": return 0.5f;
                case "getTotalExperience": return 500;
                case "getName": case "toString": return name;
                case "equals": return p == a[0];
                case "hashCode": return id.hashCode();
                default: throw new UnsupportedOperationException(m.getName());
            }
        });
    }

    private TradeSession session(Account... providers) {
        List<TradeCurrency> currencies = new ArrayList<>();
        for (int i = 0; i < providers.length; i++) {
            providers[i].balances.put(sender, new BigDecimal("100"));
            providers[i].balances.put(target, new BigDecimal("100"));
            currencies.add(new TradeCurrency("currency" + i, "test", "币" + i, providers[i], Collections.singletonList(BigDecimal.ONE)));
        }
        GoodsTrade.currencies = currencies;
        return new TradeSession(sender, target, null);
    }

    @Test public void itemOnlyTestModeDoesNotInventCurrency() {
        GoodsTrade.currencies = Collections.emptyList();
        TradeSession session = TradeSession.createTest(sender, "TestPlayer", null);
        assertTrue(session.isTestMode());
        assertTrue(session.getCurrencies().isEmpty());
        assertNull(session.getCurrency());
        assertTrue(EconomyTradeService.checkBalances(session).isSuccess());
        assertTrue(EconomyTradeService.settle(session).isSuccess());
    }

    @Test public void settlesMultipleCurrenciesInOppositeDirections() {
        Account first = new Account(), second = new Account();
        TradeSession session = session(first, second);
        session.setSenderMoney(new BigDecimal("15"));
        session.setTargetMoney(new BigDecimal("5"));
        session.nextCurrency();
        session.setTargetMoney(new BigDecimal("20"));
        assertTrue(EconomyTradeService.settle(session).isSuccess());
        assertEquals(new BigDecimal("90"), first.balances.get(sender));
        assertEquals(new BigDecimal("110"), first.balances.get(target));
        assertEquals(new BigDecimal("120"), second.balances.get(sender));
        assertEquals(new BigDecimal("80"), second.balances.get(target));
    }

    @Test public void selectionPreservesOffersAndProviderSnapshot() {
        Account first = new Account(), second = new Account();
        TradeSession session = session(first, second);
        session.setSenderMoney(BigDecimal.TEN);
        session.nextCurrency();
        assertEquals(BigDecimal.ZERO, session.getSenderMoney());
        session.setTargetMoney(BigDecimal.ONE);
        GoodsTrade.currencies = Collections.emptyList();
        session.nextCurrency();
        assertEquals(BigDecimal.TEN, session.getSenderMoney());
        assertTrue(EconomyTradeService.settle(session).isSuccess());
        assertEquals(new BigDecimal("90"), first.balances.get(sender));
    }

    @Test public void validatesAllCurrenciesBeforeAnyWithdrawal() {
        Account first = new Account(), second = new Account();
        TradeSession session = session(first, second);
        session.setSenderMoney(BigDecimal.TEN);
        session.nextCurrency();
        session.setSenderMoney(new BigDecimal("101"));
        assertEquals(EconomyTradeService.Failure.INSUFFICIENT_BALANCE, EconomyTradeService.settle(session).getFailure());
        assertEquals(0, first.writes);
        assertEquals(0, second.writes);
    }

    @Test public void balanceLossDuringCountdownRejectsPreviouslyValidOffer() {
        Account account = new Account();
        TradeSession session = session(account);
        session.setSenderMoney(BigDecimal.TEN);
        assertTrue(EconomyTradeService.checkBalances(session).isSuccess());
        account.balances.put(sender, BigDecimal.ZERO);
        assertEquals(EconomyTradeService.Failure.INSUFFICIENT_BALANCE, EconomyTradeService.settle(session).getFailure());
        assertEquals(0, account.writes);
    }

    @Test public void laterDepositFailureRefundsCurrentAndPreviousCurrencies() {
        Account first = new Account(), second = new Account();
        TradeSession session = session(first, second);
        session.setSenderMoney(BigDecimal.TEN);
        session.nextCurrency();
        session.setSenderMoney(BigDecimal.TEN);
        second.rejectDeposit = target;
        assertEquals(EconomyTradeService.Failure.TRANSACTION_FAILED, EconomyTradeService.settle(session).getFailure());
        for (Account account : Arrays.asList(first, second)) {
            assertEquals(new BigDecimal("100"), account.balances.get(sender));
            assertEquals(new BigDecimal("100"), account.balances.get(target));
        }
    }

    @Test public void failedCompensationIsReportedWithoutCreatingMoney() {
        Account first = new Account(), second = new Account();
        TradeSession session = session(first, second);
        session.setSenderMoney(BigDecimal.TEN);
        session.nextCurrency();
        session.setSenderMoney(BigDecimal.TEN);
        second.rejectDeposit = target;
        first.rejectWithdraw = target;
        assertEquals(EconomyTradeService.Failure.ROLLBACK_FAILED, EconomyTradeService.settle(session).getFailure());
        assertEquals(new BigDecimal("90"), first.balances.get(sender));
        assertEquals(new BigDecimal("110"), first.balances.get(target));
    }

    @Test public void testModeDoesNotReadOrWriteBalances() {
        Account account = new Account();
        TradeSession session = session(account);
        session.setTestMode(true);
        session.setSenderMoney(new BigDecimal("10000"));
        assertTrue(EconomyTradeService.settle(session).isSuccess());
        assertEquals(0, account.reads);
        assertEquals(0, account.writes);
    }

    @Test public void itemOnlyTradeWorksWithNoProviders() {
        assertTrue(EconomyTradeService.settle(session()).isSuccess());
    }

    @Test public void negativeOfferChargesOtherPlayer() {
        Account account = new Account();
        TradeSession session = session(account);
        session.setSenderMoney(new BigDecimal("-10"));
        assertTrue(EconomyTradeService.settle(session).isSuccess());
        assertEquals(new BigDecimal("110"), account.balances.get(sender));
    }

    @Test public void unavailableNonzeroCurrencyFailsButUnusedCurrencyDoesNot() {
        Account account = new Account();
        TradeSession session = session(account);
        account.available = false;
        assertTrue(EconomyTradeService.settle(session).isSuccess());
        session.setSenderMoney(BigDecimal.TEN);
        assertEquals(EconomyTradeService.Failure.UNAVAILABLE, EconomyTradeService.settle(session).getFailure());
        assertEquals(0, account.writes);
    }

    @Test public void experienceCanBeCombinedWithMoneyAndRefunded() {
        Account account = new Account();
        session(account);
        TradeCurrency levels = new TradeCurrency("levels", "experience", "级经验", new ExperienceEconomyProvider(),
                Collections.singletonList(BigDecimal.ONE));
        GoodsTrade.currencies = Arrays.asList(levels, GoodsTrade.currencies.get(0));
        TradeSession session = new TradeSession(sender, target, null);
        session.setSenderMoney(BigDecimal.TEN);
        session.nextCurrency();
        session.setSenderMoney(BigDecimal.TEN);
        account.rejectDeposit = target;
        assertEquals(EconomyTradeService.Failure.TRANSACTION_FAILED, EconomyTradeService.settle(session).getFailure());
        assertEquals(30, sender.getLevel());
        assertEquals(30, target.getLevel());
        assertEquals(0.5f, sender.getExp(), 0);
        assertEquals(500, sender.getTotalExperience());
        account.rejectDeposit = null;
        assertTrue(EconomyTradeService.settle(session).isSuccess());
        assertEquals(20, sender.getLevel());
        assertEquals(40, target.getLevel());
    }

    @Test public void experienceLossBeforeSettlementCancelsTrade() {
        GoodsTrade.currencies = Collections.singletonList(new TradeCurrency("levels", "experience", "级经验",
                new ExperienceEconomyProvider(), Collections.singletonList(BigDecimal.ONE)));
        TradeSession session = new TradeSession(sender, target, null);
        session.setSenderMoney(BigDecimal.TEN);
        assertTrue(EconomyTradeService.checkBalances(session).isSuccess());
        sender.setLevel(5);
        assertEquals(EconomyTradeService.Failure.INSUFFICIENT_BALANCE, EconomyTradeService.settle(session).getFailure());
        assertEquals(5, sender.getLevel());
        assertEquals(30, target.getLevel());
    }

    @Test public void experienceRejectsFractionsAndOverflowWithoutChangingLevel() {
        ExperienceEconomyProvider provider = new ExperienceEconomyProvider();
        assertFalse(provider.withdraw(sender, new BigDecimal("0.5")).isSuccess());
        assertFalse(provider.deposit(sender, new BigDecimal("2147483647")).isSuccess());
        assertFalse(provider.withdraw(sender, new BigDecimal("31")).isSuccess());
        assertEquals(30, sender.getLevel());
        assertTrue(provider.withdraw(sender, new BigDecimal("30")).isSuccess());
        assertEquals(0, sender.getLevel());
        assertEquals(0.5f, sender.getExp(), 0);
    }

    @Test public void testModeLeavesExperienceUnchanged() {
        GoodsTrade.currencies = Collections.singletonList(new TradeCurrency("levels", "experience", "级经验",
                new ExperienceEconomyProvider(), Collections.singletonList(BigDecimal.ONE)));
        TradeSession session = TradeSession.createTest(sender, "TestPlayer", null);
        session.setSenderMoney(BigDecimal.TEN);
        assertTrue(EconomyTradeService.settle(session).isSuccess());
        assertEquals(30, sender.getLevel());
    }

    @Test public void requestsMustMatchSenderAndRemainUnexpired() {
        Player unrelated = player("unrelated");
        TradeManager.pendingRequests.clear();
        try {
            TradeManager.pendingRequests.put(target.getUniqueId(), Collections.singletonList(new TradeRequest(sender, target, 30000)));
            assertTrue(TradeManager.hasPendingRequest(sender, target));
            assertFalse(TradeManager.hasPendingRequest(unrelated, target));
            assertFalse(TradeManager.hasPendingRequest(target, sender));
            TradeManager.pendingRequests.put(target.getUniqueId(), Collections.singletonList(new TradeRequest(sender, target, -1)));
            assertFalse(TradeManager.hasPendingRequest(sender, target));
        } finally {
            TradeManager.pendingRequests.clear();
        }
    }

    private static class Account implements EconomyProvider {
        final Map<Player, BigDecimal> balances = new HashMap<>();
        Player rejectDeposit, rejectWithdraw;
        boolean available = true;
        int reads, writes;
        @Override public boolean isAvailable() { return available; }
        @Override public boolean has(Player player, BigDecimal amount) { reads++; return balances.get(player).compareTo(amount) >= 0; }
        @Override public EconomyTransactionResult withdraw(Player player, BigDecimal amount) {
            writes++;
            if (player == rejectWithdraw || !has(player, amount)) return EconomyTransactionResult.failure("rejected");
            balances.put(player, balances.get(player).subtract(amount));
            return EconomyTransactionResult.success();
        }
        @Override public EconomyTransactionResult deposit(Player player, BigDecimal amount) {
            writes++;
            if (player == rejectDeposit) return EconomyTransactionResult.failure("rejected");
            balances.put(player, balances.get(player).add(amount));
            return EconomyTransactionResult.success();
        }
        @Override public String format(BigDecimal amount) { return amount.toPlainString(); }
        @Override public String getName() { return "test"; }
    }
}
