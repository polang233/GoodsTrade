package cc.sbsj.polang.goodstrade.trade;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;

public class TradeRestrictionsTest {
    private final YamlConfiguration config = new YamlConfiguration();
    private final World world = world("world");
    private final World nether = world("world_nether");
    private static World world(String name) {
        return (World) Proxy.newProxyInstance(World.class.getClassLoader(), new Class<?>[]{World.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getName": return name;
                        case "equals": return proxy == args[0];
                        case "hashCode": return name.hashCode();
                        default: throw new UnsupportedOperationException(method.getName());
                    }
                });
    }
    private String check(World other, double x, double y, boolean active) {
        return TradeRestrictions.check(config, new Location(world, 0, 0, 0),
                new Location(other, x, y, 0), active);
    }
    @Test public void shippedDefaultsMatchItemTradingAndDistancePolicy() throws Exception {
        try (java.io.Reader reader = new java.io.InputStreamReader(
                getClass().getResourceAsStream("/config.yml"), java.nio.charset.StandardCharsets.UTF_8)) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
            assertFalse(defaults.getBoolean("Trade.Economy.Enable"));
            assertFalse(defaults.getBoolean("Trade.Safe.Close-On-Damage"));
            assertTrue(defaults.getBoolean("Trade.Distance.Same-World"));
            assertEquals(4, defaults.getDouble("Trade.Distance.Start"), 0);
            assertEquals(8, defaults.getDouble("Trade.Distance.Trading"), 0);
            assertEquals(Collections.singletonList("*"),
                    defaults.getStringList("Trade.Enabled-Worlds"));
        }
    }
    @Test public void missingOrWildcardWorldListAllowsCustomAndFutureWorlds() {
        assertTrue(TradeRestrictions.isWorldEnabled(config, "arena"));
        config.set("Trade.Enabled-Worlds", Collections.singletonList("*"));
        assertTrue(TradeRestrictions.isWorldEnabled(config, "new_world"));
        World custom = world("custom_world");
        for (boolean active : new boolean[]{false, true}) {
            assertNull(TradeRestrictions.check(config, new Location(custom, 0, 0, 0),
                    new Location(custom, 1, 0, 0), active));
        }
        config.set("Trade.Enabled-Worlds", Collections.singletonList("world"));
        assertFalse(TradeRestrictions.isWorldEnabled(config, "arena"));
    }
    @Test public void defaultStartBoundaryAndVerticalDistance() {
        assertNull(check(world, 4, 0, false));
        assertEquals("trade-status.start-too-far", check(world, 4.01, 0, false));
        assertEquals("trade-status.start-too-far", check(world, 0, 4.01, false));
    }
    @Test public void activeTradeHasIndependentBoundary() {
        assertEquals("trade-status.start-too-far", check(world, 6, 0, false));
        assertNull(check(world, 6, 0, true));
        assertNull(check(world, 8, 0, true));
        assertEquals("trade-status.cancelled-by-distance", check(world, 8.01, 0, true));
        config.set("Trade.Distance.Start", 2);
        config.set("Trade.Distance.Trading", 12);
        assertEquals("trade-status.start-too-far", check(world, 3, 0, false));
        assertNull(check(world, 12, 0, true));
    }
    @Test public void disabledWorldAlwaysRejectsEitherParticipant() {
        config.set("Trade.Distance.Same-World", false);
        config.set("Trade.Distance.Start", 0);
        config.set("Trade.Distance.Trading", 0);
        for (boolean active : new boolean[]{false, true}) {
            config.set("Trade.Enabled-Worlds", Collections.singletonList("world"));
            assertEquals("trade-status.world-disabled", check(world("arena"), 0, 0, active));
            config.set("Trade.Enabled-Worlds", Collections.singletonList("world_nether"));
            assertEquals("trade-status.world-disabled", check(nether, 0, 0, active));
            config.set("Trade.Enabled-Worlds", null);
        }
        config.set("Trade.Enabled-Worlds", Collections.emptyList());
        assertEquals("trade-status.world-disabled", check(world, 0, 0, false));
    }
    @Test public void crossWorldRequiresExplicitOptOutOfWorldAndDistanceLimits() {
        assertEquals("trade-status.same-world-required", check(nether, 0, 0, false));
        config.set("Trade.Distance.Same-World", false);
        assertEquals("trade-status.same-world-required", check(nether, 0, 0, false));
        config.set("Trade.Distance.Start", 0);
        assertNull(check(nether, 0, 0, false));
        assertEquals("trade-status.same-world-required", check(nether, 0, 0, true));
        config.set("Trade.Distance.Trading", 0);
        assertNull(check(nether, 0, 0, true));
        config.set("Trade.Distance.Same-World", true);
        assertEquals("trade-status.same-world-required", check(nether, 0, 0, true));
    }
    @Test public void customWorldAndUnlimitedDistance() {
        config.set("Trade.Enabled-Worlds", Arrays.asList("world", "arena"));
        assertTrue(TradeRestrictions.isWorldEnabled(config, "arena"));
        config.set("Trade.Distance.Start", 0);
        assertNull(check(world, 100000, 0, false));
        assertEquals("trade-status.cancelled-by-distance", check(world, 100000, 0, true));
    }
    @Test public void invalidLimitsUseDefaults() {
        for (double value : new double[]{-1, Double.NaN, Double.POSITIVE_INFINITY}) {
            config.set("Trade.Distance.Start", value);
            config.set("Trade.Distance.Trading", value);
            assertEquals("trade-status.start-too-far", check(world, 5, 0, false));
            assertEquals("trade-status.cancelled-by-distance", check(world, 9, 0, true));
        }
    }
}