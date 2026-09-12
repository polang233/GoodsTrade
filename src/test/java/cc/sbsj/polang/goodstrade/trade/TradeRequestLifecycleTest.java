package cc.sbsj.polang.goodstrade.trade;

import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.util.UUID;
import static org.junit.Assert.*;

public class TradeRequestLifecycleTest {
    private final Player a = player("A"), b = player("B"), c = player("C"), d = player("D");
    private static Player player(String name) {
        UUID id = UUID.nameUUIDFromBytes(name.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
                (p, m, args) -> {
                    switch (m.getName()) {
                        case "getUniqueId": return id;
                        case "getName": return name;
                        case "equals": return p == args[0];
                        case "hashCode": return id.hashCode();
                        default: throw new UnsupportedOperationException(m.getName());
                    }
                });
    }
    private void request(Player sender, Player target) {
        TradeManager.pendingRequests.computeIfAbsent(target.getUniqueId(), key -> new java.util.ArrayList<>())
                .add(new TradeRequest(sender, target, 30000));
    }
    @After public void reset() {
        for (Player p : new Player[]{a, b, c, d}) TradeManager.removeSession(p);
        TradeManager.pendingRequests.clear();
    }
    @Test public void acceptingOneTradeInvalidatesAllParticipantRequestsOnly() {
        request(a, b); request(a, c); request(d, a); request(c, d); request(b, d);
        TradeManager.createSession(a, c, null);
        assertTrue(TradeManager.isTrade(a));
        assertTrue(TradeManager.isTrade(c));
        assertFalse(TradeManager.hasPendingRequest(a, b));
        assertFalse(TradeManager.hasPendingRequest(a, c));
        assertFalse(TradeManager.hasPendingRequest(d, a));
        assertFalse(TradeManager.hasPendingRequest(c, d));
        assertTrue(TradeManager.hasPendingRequest(b, d));
        TradeManager.removeSession(a);
        assertFalse(TradeManager.hasPendingRequest(a, b));
    }
    @Test public void testModeAlsoInvalidatesIncomingAndOutgoingRequests() {
        request(a, b); request(c, a); request(b, d);
        TradeManager.createTestSession(a, "TestPlayer", null);
        assertFalse(TradeManager.hasPendingRequest(a, b));
        assertFalse(TradeManager.hasPendingRequest(c, a));
        assertTrue(TradeManager.hasPendingRequest(b, d));
    }
    @Test public void cooldownAndRequestExpiryHaveIndependentBoundaries() {
        long[] now = {1000};
        RequestCooldown cooldown = new RequestCooldown(() -> now[0]);
        TradeRequest request = new TradeRequest(a, b, 30000, now[0]);
        cooldown.record(a.getUniqueId(), 5000);
        now[0] = 5999;
        assertEquals(1, cooldown.remaining(a.getUniqueId()));
        assertEquals(0, cooldown.remaining(c.getUniqueId()));
        now[0] = 6000;
        assertEquals(0, cooldown.remaining(a.getUniqueId()));
        assertFalse(request.isExpired(now[0]));
        assertFalse(request.isExpired(30999));
        assertTrue(request.isExpired(31000));
    }
    @Test public void clearingRequestsDoesNotResetSenderCooldown() {
        RequestCooldown cooldown = new RequestCooldown(() -> 1000L);
        request(a, b);
        cooldown.record(a.getUniqueId(), 5000);
        TradeManager.cancelAllRequests(a);
        assertEquals(5000, cooldown.remaining(a.getUniqueId()));
        cooldown.clear();
        assertEquals(0, cooldown.remaining(a.getUniqueId()));
        cooldown.record(a.getUniqueId(), 0);
        assertEquals(0, cooldown.remaining(a.getUniqueId()));
    }
    @Test public void reloadClearsRequestsEvenWithoutActiveTrades() {
        request(a, b);
        TradeManager.stopAllTrades();
        assertFalse(TradeManager.hasPendingRequest(a, b));
    }
}
