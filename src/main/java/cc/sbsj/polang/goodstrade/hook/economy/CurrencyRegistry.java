package cc.sbsj.polang.goodstrade.hook.economy;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.hook.economy.provider.ExperienceEconomyProvider;
import cc.sbsj.polang.goodstrade.hook.economy.provider.ExcellentEconomyProvider;
import cc.sbsj.polang.goodstrade.hook.economy.provider.PlayerPointsEconomyProvider;
import cc.sbsj.polang.goodstrade.hook.economy.provider.VaultEconomyProvider;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class CurrencyRegistry {
    private CurrencyRegistry() { }

    public static List<TradeCurrency> load(GoodsTrade plugin) {
        if (!GoodsTrade.config.isEconomyEnabled()) return Collections.emptyList();
        List<TradeCurrency> currencies = new ArrayList<>();
        // 旧配置继续使用 Vault；显式配置了币种列表后不偷偷添加其它账户。
        ConfigurationSection section = plugin.getConfig().contains("Trade.Economy.Currencies", true)
                ? plugin.getConfig().getConfigurationSection("Trade.Economy.Currencies") : null;
        if (section == null && plugin.getConfig().contains("Trade.Economy.Currencies", true)) {
            plugin.getLogger().warning("Trade.Economy.Currencies 必须是配置节，货币交易已关闭。");
            return Collections.emptyList();
        }
        if (section == null) {
            add(plugin, currencies, "vault", "vault", "", "", GoodsTrade.config.getEconomyButtonAmounts());
        } else {
            Set<String> accounts = new HashSet<>();
            for (String id : section.getKeys(false)) {
                ConfigurationSection entry = section.getConfigurationSection(id);
                if (entry == null || !entry.getBoolean("Enable", true)) continue;
                String provider = entry.getString("Provider", "vault").toLowerCase(Locale.ROOT);
                String currency = entry.getString("Currency", "");
                String account = provider + ":" + (provider.equals("excellenteconomy") ? currency : "");
                if (!accounts.add(account)) {
                    plugin.getLogger().warning("重复的货币账户，已跳过: " + id);
                    continue;
                }
                List<BigDecimal> amounts = new ArrayList<>();
                List<?> configured = entry.getList("Amounts");
                if (configured == null) amounts.addAll(GoodsTrade.config.getEconomyButtonAmounts());
                else for (Object value : configured) {
                    try {
                        BigDecimal amount = new BigDecimal(String.valueOf(value));
                        if (amount.signum() <= 0) throw new NumberFormatException();
                        if (amounts.size() < 4) amounts.add(amount);
                    } catch (NumberFormatException exception) {
                        plugin.getLogger().warning("币种 " + id + " 的按钮金额无效: " + value);
                    }
                }
                String name = entry.getString("Name", id);
                if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name)).trim().isEmpty()) name = id;
                add(plugin, currencies, id, provider, currency, name, amounts);
            }
        }
        return Collections.unmodifiableList(currencies);
    }

    private static void add(GoodsTrade plugin, List<TradeCurrency> currencies, String id,
                            String type, String currencyId, String name, List<BigDecimal> amounts) {
        try {
            EconomyProvider provider;
            switch (type) {
                case "experience":
                    provider = new ExperienceEconomyProvider();
                    break;
                case "vault":
                    require(plugin, "Vault");
                    provider = VaultEconomyProvider.hook(plugin);
                    if (provider == null) throw new IllegalStateException("Vault 未注册经济服务");
                    break;
                case "playerpoints":
                    provider = new PlayerPointsEconomyProvider(require(plugin, "PlayerPoints"));
                    break;
                case "excellenteconomy":
                    if (currencyId.isEmpty()) throw new IllegalArgumentException("必须填写 Currency");
                    provider = new ExcellentEconomyProvider(require(plugin, "ExcellentEconomy"), currencyId);
                    break;
                default: throw new IllegalArgumentException("未知 Provider: " + type);
            }
            List<BigDecimal> valid = new ArrayList<>();
            for (BigDecimal amount : amounts) {
                if (provider.supports(amount)) valid.add(amount);
                else plugin.getLogger().warning("币种 " + id + " 不支持按钮金额: " + amount);
            }
            if (valid.isEmpty()) throw new IllegalArgumentException("没有可用的按钮金额");
            currencies.add(new TradeCurrency(id, ChatColor.translateAlternateColorCodes('&', name), provider, valid));
            plugin.getLogger().info("交易货币已连接: " + id + " -> " + provider.getName());
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            plugin.getLogger().warning("交易货币 " + id + " 不可用，已跳过: " + exception);
        }
    }

    private static Plugin require(GoodsTrade plugin, String name) {
        Plugin dependency = plugin.getServer().getPluginManager().getPlugin(name);
        if (dependency == null || !dependency.isEnabled()) throw new IllegalStateException(name + " 未启用");
        return dependency;
    }
}
