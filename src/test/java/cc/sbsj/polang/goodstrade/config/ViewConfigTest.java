package cc.sbsj.polang.goodstrade.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/** 使用真实 ItemStack 和配置加载器，物品元数据由内存 ItemFactory 提供。 */
public class ViewConfigTest {
    private static Server previousServer;

    @BeforeClass public static void installItemFactory() throws Exception {
        previousServer = Bukkit.getServer();
        ItemFactory factory = (ItemFactory) Proxy.newProxyInstance(ItemFactory.class.getClassLoader(),
                new Class<?>[]{ItemFactory.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getItemMeta": return args[0] == Material.AIR ? null : new Meta().proxy();
                        case "isApplicable": return true;
                        case "asMetaFor": return args[0];
                        case "updateMaterial": return args[1];
                        case "equals": return args[0] == args[1];
                        default: throw new UnsupportedOperationException(method.getName());
                    }
                });
        Server server = (Server) Proxy.newProxyInstance(Server.class.getClassLoader(), new Class<?>[]{Server.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getItemFactory": return factory;
                        case "getVersion": return "Test server (MC: 1.16.5)";
                        case "getBukkitVersion": return "1.16.5-R0.1-SNAPSHOT";
                        case "getName": return "GoodsTrade test";
                        case "getLogger": return Logger.getLogger("GoodsTrade test");
                        default: throw new UnsupportedOperationException(method.getName());
                    }
                });
        setServer(server);
    }

    @AfterClass public static void restoreServer() throws Exception { setServer(previousServer); }

    private static void setServer(Server server) throws Exception {
        Field field = Bukkit.class.getDeclaredField("server");
        field.setAccessible(true);
        field.set(null, server);
    }

    private ItemStack defaultButton() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT, 3);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6调整：%amount%");
        meta.setLore(Arrays.asList("§a增加 %amount%", "§c减少 %amount%"));
        item.setItemMeta(meta);
        return item;
    }

    @Test public void materialOnlyPreservesNameLoreAndAmount() {
        ItemStack original = defaultButton();
        YamlConfiguration config = new YamlConfiguration();
        config.set("button.Money.material", "emerald");
        ItemStack actual = ViewConfig.loadButtonItem(config, "Money", original);
        assertEquals(Material.EMERALD, actual.getType());
        assertEquals(3, actual.getAmount());
        assertEquals(original.getItemMeta().getDisplayName(), actual.getItemMeta().getDisplayName());
        assertEquals(original.getItemMeta().getLore(), actual.getItemMeta().getLore());
        assertEquals(Material.GOLD_INGOT, original.getType());
    }

    @Test public void explicitTextOverridesInheritedText() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("button.Money.material", "diamond");
        config.set("button.Money.name", "&b%currency%");
        config.set("button.Money.lore", Collections.singletonList("&e%amount%"));
        ItemStack actual = ViewConfig.loadButtonItem(config, "Money", defaultButton());
        assertEquals("§b%currency%", actual.getItemMeta().getDisplayName());
        assertEquals(Collections.singletonList("§e%amount%"), actual.getItemMeta().getLore());
    }

    @Test public void defaultMarkersPreserveTextAfterMaterialChange() {
        ItemStack original = defaultButton();
        YamlConfiguration config = new YamlConfiguration();
        config.set("button.Money.material", "emerald");
        config.set("button.Money.name", "@default@");
        config.set("button.Money.lore", Collections.singletonList("@default@"));
        ItemStack actual = ViewConfig.loadButtonItem(config, "Money", original);
        assertEquals(original.getItemMeta().getDisplayName(), actual.getItemMeta().getDisplayName());
        assertEquals(original.getItemMeta().getLore(), actual.getItemMeta().getLore());
    }

    @Test public void tierMaterialInheritsSharedTextAndModelData() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("button.Money.material", "emerald");
        config.set("button.Money.name", "&d支付 %amount%");
        config.set("button.Money.lore", Collections.singletonList("&e%currency%"));
        config.set("button.Money.custom_model_data", 123);
        config.set("button.Money-1.material", "diamond");
        ItemStack shared = ViewConfig.loadButtonItem(config, "Money", defaultButton());
        ItemStack tier = ViewConfig.loadButtonItem(config, "Money-1", shared);
        assertEquals(Material.DIAMOND, tier.getType());
        assertEquals(shared.getItemMeta().getDisplayName(), tier.getItemMeta().getDisplayName());
        assertEquals(shared.getItemMeta().getLore(), tier.getItemMeta().getLore());
        assertEquals(123, tier.getItemMeta().getCustomModelData());
        assertEquals(Material.EMERALD, shared.getType());
    }

    @Test public void explicitEmptyLoreStillClearsInheritedLore() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("button.Money.material", "emerald");
        config.set("button.Money.lore", Collections.emptyList());
        ItemStack actual = ViewConfig.loadButtonItem(config, "Money", defaultButton());
        assertFalse(actual.getItemMeta().hasLore());
        assertTrue(actual.getItemMeta().hasDisplayName());
    }

    @Test public void builtInMaterialsFollowProviderWithoutNewConfig() {
        YamlConfiguration config = new YamlConfiguration();
        assertEquals(Material.GOLD_INGOT, ViewConfig.loadCurrencyButton(config, "vault", "coins", 0, defaultButton()).getType());
        assertEquals(Material.EMERALD, ViewConfig.loadCurrencyButton(config, "playerpoints", "points", 0, defaultButton()).getType());
        assertEquals(Material.EXPERIENCE_BOTTLE, ViewConfig.loadCurrencyButton(config, "experience", "levels", 0, defaultButton()).getType());
        assertEquals(Material.SUNFLOWER, ViewConfig.loadCurrencyButton(config, "excellenteconomy", "tokens", 0, defaultButton()).getType());
    }

    @Test public void providerDefaultsCanBeCustomizedWithoutLosingText() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("currency-defaults.experience.material", "lapis_lazuli");
        ItemStack actual = ViewConfig.loadCurrencyButton(config, "experience", "levels", 0, defaultButton());
        assertEquals(Material.LAPIS_LAZULI, actual.getType());
        assertEquals(defaultButton().getItemMeta().getDisplayName(), actual.getItemMeta().getDisplayName());
        assertEquals(defaultButton().getItemMeta().getLore(), actual.getItemMeta().getLore());
    }

    @Test public void existingGlobalStylesOverrideProviderDefaults() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("button.Money.material", "diamond");
        config.set("button.Money.name", "&d支付 %amount%");
        config.set("button.Money-2.material", "diamond_block");
        assertEquals(Material.DIAMOND, ViewConfig.loadCurrencyButton(config, "experience", "levels", 0, defaultButton()).getType());
        ItemStack tier = ViewConfig.loadCurrencyButton(config, "experience", "levels", 1, defaultButton());
        assertEquals(Material.DIAMOND_BLOCK, tier.getType());
        assertEquals("§d支付 %amount%", tier.getItemMeta().getDisplayName());
    }

    @Test public void currencyOverridesAreIsolatedByConfiguredId() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("currency-buttons.gems.Money.material", "diamond");
        config.set("currency-buttons.gems.Money.name", "&b%currency%");
        ItemStack gems = ViewConfig.loadCurrencyButton(config, "excellenteconomy", "gems", 0, defaultButton());
        ItemStack coins = ViewConfig.loadCurrencyButton(config, "excellenteconomy", "coins", 0, defaultButton());
        assertEquals(Material.DIAMOND, gems.getType());
        assertEquals("§b%currency%", gems.getItemMeta().getDisplayName());
        assertEquals(Material.SUNFLOWER, coins.getType());
        assertEquals(defaultButton().getItemMeta().getDisplayName(), coins.getItemMeta().getDisplayName());
    }

    @Test public void currencyTierOverridesGlobalTierAndInheritsOtherFields() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("button.Money-2.material", "iron_ingot");
        config.set("button.Money-2.lore", Collections.singletonList("&e%amount%"));
        config.set("currency-buttons.points.Money.material", "emerald");
        config.set("currency-buttons.points.Money.custom_model_data", 123);
        config.set("currency-buttons.points.Money-2.material", "emerald_block");
        ItemStack tier = ViewConfig.loadCurrencyButton(config, "playerpoints", "points", 1, defaultButton());
        assertEquals(Material.EMERALD_BLOCK, tier.getType());
        assertEquals(Collections.singletonList("§e%amount%"), tier.getItemMeta().getLore());
        assertEquals(123, tier.getItemMeta().getCustomModelData());
        assertEquals(Material.EMERALD, ViewConfig.loadCurrencyButton(config, "playerpoints", "points", 0, defaultButton()).getType());
    }

    @Test public void reloadingStyleDoesNotReusePreviousCurrencyTemplate() {
        YamlConfiguration config = new YamlConfiguration();
        ItemStack base = defaultButton();
        config.set("currency-buttons.points.Money.material", "diamond");
        assertEquals(Material.DIAMOND, ViewConfig.loadCurrencyButton(config, "playerpoints", "points", 0, base).getType());
        config.set("currency-buttons.points", null);
        assertEquals(Material.EMERALD, ViewConfig.loadCurrencyButton(config, "playerpoints", "points", 0, base).getType());
        assertEquals(Material.GOLD_INGOT, base.getType());
    }

    private static final class Meta implements InvocationHandler {
        private String name;
        private List<String> lore;
        private Integer model;

        private ItemMeta proxy() {
            return (ItemMeta) Proxy.newProxyInstance(ItemMeta.class.getClassLoader(), new Class<?>[]{ItemMeta.class}, this);
        }

        @Override public Object invoke(Object proxy, Method method, Object[] args) {
            switch (method.getName()) {
                case "hasDisplayName": return name != null;
                case "getDisplayName": return name;
                case "setDisplayName": name = (String) args[0]; return null;
                case "hasLore": return lore != null && !lore.isEmpty();
                case "getLore": return lore == null ? null : new ArrayList<>(lore);
                case "setLore":
                    lore = args[0] == null ? null : new ArrayList<>();
                    if (args[0] != null) for (Object line : (List<?>) args[0]) lore.add((String) line);
                    return null;
                case "hasCustomModelData": return model != null;
                case "getCustomModelData": return model;
                case "setCustomModelData": model = (Integer) args[0]; return null;
                case "clone":
                    Meta copy = new Meta();
                    copy.name = name;
                    copy.lore = lore == null ? null : new ArrayList<>(lore);
                    copy.model = model;
                    return copy.proxy();
                default: throw new UnsupportedOperationException(method.getName());
            }
        }
    }
}
