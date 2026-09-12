package cc.sbsj.polang.goodstrade.config;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.util.ItemBlackList;
import org.bukkit.configuration.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import cc.sbsj.polang.goodstrade.trade.TradeRestrictions;
import org.bukkit.Location;
import java.util.Collections;
import java.util.List;

public class Config {
    private Configuration config;
    private ItemBlackList itemBlackList;
    private List<BigDecimal> economyButtonAmounts;

    public Config(GoodsTrade plugin) {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
        loadItemBlackList();
        loadEconomyButtonAmounts();
    }

    public int getWaitTime() {
        // 倒计时也用作按钮堆叠数量；0 表示直接完成确认。
        return Math.max(0, Math.min(64, config.getInt("Trade.Wait-Time", 5)));
    }

    public void reload() {
        GoodsTrade.instance.reloadConfig();
        config = GoodsTrade.instance.getConfig();
        GoodsTrade.lang.load();
        loadEconomyButtonAmounts();
        GoodsTrade.instance.refreshEconomy();
        ViewConfig.load(GoodsTrade.instance);
        loadItemBlackList();
    }

    public long getRequestCooldownMillis() {
        return requestSeconds("Trade.Request.Cooldown", 5, 0) * 1000L;
    }

    public long getRequestExpiryMillis() {
        return requestSeconds("Trade.Request.Expire", 30, 1) * 1000L;
    }

    private long requestSeconds(String path, long fallback, long minimum) {
        long seconds = config.getLong(path, fallback);
        return seconds < minimum || seconds > 86400 ? fallback : seconds;
    }

    public boolean isEnabledShiftClick() {
        return config.getBoolean("Trade.Triggers.Shift-Right-Click", true);
    }

    public boolean isSafeDamage() {
        return config.getBoolean("Trade.Safe.Damage", false);
    }

    public boolean isCloseOnDamage() {
        return config.getBoolean("Trade.Safe.Close-On-Damage", false);
    }

    public boolean isWorldEnabled(String worldName) {
        return TradeRestrictions.isWorldEnabled(config, worldName);
    }

    public String checkTradeLocations(Location sender, Location target, boolean active) {
        return TradeRestrictions.check(config, sender, target, active);
    }

    public boolean isSafeMove() {
        return config.getBoolean("Trade.Safe.Move", false);
    }

    public boolean isEconomyEnabled() {
        return config.getBoolean("Trade.Economy.Enable", false);
    }

    public boolean isNegativeEconomyAllowed() {
        return config.getBoolean("Trade.Economy.Allow-Negative", false);
    }

    public List<BigDecimal> getEconomyButtonAmounts() {
        return economyButtonAmounts;
    }

    public ItemBlackList getItemBlackList() {
        return itemBlackList;
    }

    private void loadItemBlackList() {
        itemBlackList = ItemBlackList.fromConfig(config);
    }

    private void loadEconomyButtonAmounts() {
        List<?> configured = config.getList("Trade.Economy.Amounts");
        List<BigDecimal> amounts = new ArrayList<>();
        if (configured != null) {
            for (Object value : configured) {
                if (amounts.size() == 4) {
                    GoodsTrade.instance.getLogger().warning("Trade.Economy.Amounts 最多支持 4 个金额按钮，多余配置已忽略。");
                    break;
                }
                try {
                    BigDecimal amount = new BigDecimal(String.valueOf(value)).stripTrailingZeros();
                    double vaultAmount = amount.doubleValue();
                    if (amount.signum() <= 0 || vaultAmount <= 0
                            || Double.isInfinite(vaultAmount) || Double.isNaN(vaultAmount)) {
                        throw new NumberFormatException();
                    }
                    amounts.add(amount);
                } catch (NumberFormatException ignored) {
                    GoodsTrade.instance.getLogger().warning("Trade.Economy.Amounts 中存在无效金额，已忽略: " + value);
                }
            }
        }
        if (amounts.isEmpty()) {
            amounts.add(new BigDecimal("1000"));
            amounts.add(new BigDecimal("10000"));
        }
        economyButtonAmounts = Collections.unmodifiableList(amounts);
    }
}
