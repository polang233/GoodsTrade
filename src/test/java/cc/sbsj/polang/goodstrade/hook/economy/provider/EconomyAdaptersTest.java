package cc.sbsj.polang.goodstrade.hook.economy.provider;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.junit.Assert.*;

/** API 契约替身测试，不代表已经启动第三方插件进行联调。 */
public class EconomyAdaptersTest {
    private final UUID id = UUID.randomUUID();
    private final Player player = (Player) Proxy.newProxyInstance(Player.class.getClassLoader(),
            new Class<?>[]{Player.class}, (p, m, a) -> {
                if (m.getName().equals("getUniqueId")) return id;
                throw new UnsupportedOperationException(m.getName());
            });

    public interface ApiPlugin extends Plugin { Object getAPI(); }

    private Plugin plugin(Object api, boolean enabled) {
        return (Plugin) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ApiPlugin.class}, (p, m, a) -> {
            switch (m.getName()) {
                case "getAPI": return api;
                case "isEnabled": return enabled;
                case "getName": return "TestEconomy";
                default: throw new UnsupportedOperationException(m.getName());
            }
        });
    }

    @Test public void pointsUseUuidAndRejectFractionalOrOverflowAmounts() throws Exception {
        PointsApi api = new PointsApi();
        api.points.put(id, 100);
        PlayerPointsEconomyProvider provider = new PlayerPointsEconomyProvider(plugin(api, true));
        assertTrue(provider.supports(new BigDecimal("10.0")));
        assertFalse(provider.supports(new BigDecimal("0.5")));
        assertFalse(provider.supports(new BigDecimal("2147483648")));
        assertFalse(provider.supports(new BigDecimal("-1")));
        assertTrue(provider.withdraw(player, BigDecimal.TEN).isSuccess());
        assertEquals(90, api.look(id));
        assertTrue(provider.deposit(player, BigDecimal.TEN).isSuccess());
        assertEquals(100, api.look(id));
        assertFalse(provider.withdraw(player, new BigDecimal("101")).isSuccess());
        api.points.put(id, Integer.MAX_VALUE);
        assertFalse(provider.deposit(player, BigDecimal.ONE).isSuccess());
        assertEquals(Integer.MAX_VALUE, api.look(id));
    }

    @Test public void pointsPropagateRejectedWrites() throws Exception {
        PointsApi api = new PointsApi();
        api.points.put(id, 100);
        api.reject = true;
        PlayerPointsEconomyProvider provider = new PlayerPointsEconomyProvider(plugin(api, true));
        assertFalse(provider.withdraw(player, BigDecimal.ONE).isSuccess());
        assertFalse(provider.deposit(player, BigDecimal.ONE).isSuccess());
        assertEquals(100, api.look(id));
    }

    @Test public void excellentUsesConfiguredCurrencyAndProtectsIntegerAndBalanceLimits() throws Exception {
        ExcellentApi api = new ExcellentApi();
        ExcellentEconomyProvider provider = new ExcellentEconomyProvider(plugin(api, true), "tokens");
        assertFalse(provider.supports(new BigDecimal("0.5")));
        assertTrue(provider.withdraw(player, BigDecimal.TEN).isSuccess());
        assertEquals(90, api.balance, 0);
        assertEquals("tokens", api.lastCurrency);
        assertTrue(provider.deposit(player, BigDecimal.TEN).isSuccess());
        assertFalse(provider.deposit(player, BigDecimal.ONE).isSuccess());
        assertEquals(100, api.balance, 0);
        api.currency.integer = false;
        assertTrue(provider.withdraw(player, new BigDecimal("0.5")).isSuccess());
        assertEquals(99.5, api.balance, 0);
        assertTrue(provider.deposit(player, new BigDecimal("0.5")).isSuccess());
    }

    @Test public void excellentStopsWhenOperationsDisabledOrCurrencyRemoved() throws Exception {
        ExcellentApi api = new ExcellentApi();
        ExcellentEconomyProvider provider = new ExcellentEconomyProvider(plugin(api, true), "tokens");
        api.enabled = false;
        assertFalse(provider.isAvailable());
        assertFalse(provider.withdraw(player, BigDecimal.ONE).isSuccess());
        api.enabled = true;
        api.currency = null;
        assertFalse(provider.isAvailable());
        assertFalse(provider.deposit(player, BigDecimal.ONE).isSuccess());
        assertEquals(100, api.balance, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void excellentUnknownCurrencyCannotHook() throws Exception {
        new ExcellentEconomyProvider(plugin(new ExcellentApi(), true), "missing");
    }

    public static class PointsApi {
        final Map<UUID, Integer> points = new HashMap<>();
        boolean reject;
        public int look(UUID id) { return points.getOrDefault(id, 0); }
        public boolean take(UUID id, int amount) { return give(id, -amount); }
        public boolean give(UUID id, int amount) {
            if (reject) return false;
            points.put(id, look(id) + amount);
            return true;
        }
    }

    public static class Currency {
        boolean integer = true;
        public boolean isInteger() { return integer; }
        public boolean isUnderLimit(double amount) { return amount <= 100; }
    }

    public static class ExcellentApi {
        Currency currency = new Currency();
        double balance = 100;
        boolean enabled = true;
        String lastCurrency;
        public Currency getCurrency(String id) { return id.equals("tokens") ? currency : null; }
        public boolean canPerformOperations() { return enabled; }
        public double getBalance(Player player, String id) { lastCurrency = id; return balance; }
        public boolean withdraw(Player player, String id, double amount) { lastCurrency = id; balance -= amount; return true; }
        public boolean deposit(Player player, String id, double amount) { lastCurrency = id; balance += amount; return true; }
    }
}
