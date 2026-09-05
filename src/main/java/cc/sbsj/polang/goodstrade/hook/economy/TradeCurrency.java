package cc.sbsj.polang.goodstrade.hook.economy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 一次配置加载得到的币种快照，交易期间不替换提供者。 */
public final class TradeCurrency {
    private final String id;
    private final String providerType;
    private final String name;
    private final EconomyProvider provider;
    private final List<BigDecimal> amounts;

    public TradeCurrency(String id, String providerType, String name, EconomyProvider provider, List<BigDecimal> amounts) {
        this.id = id;
        this.providerType = providerType;
        this.name = name;
        this.provider = provider;
        this.amounts = Collections.unmodifiableList(new ArrayList<>(amounts));
    }

    public String getId() { return id; }
    public String getProviderType() { return providerType; }
    public String getName() { return name; }
    public EconomyProvider getProvider() { return provider; }
    public List<BigDecimal> getAmounts() { return amounts; }
    public String format(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString() + " " + name;
    }
}
