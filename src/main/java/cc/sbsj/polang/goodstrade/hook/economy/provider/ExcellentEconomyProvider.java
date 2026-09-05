package cc.sbsj.polang.goodstrade.hook.economy.provider;

import cc.sbsj.polang.goodstrade.hook.economy.EconomyTransactionResult;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/** 对接 ExcellentEconomy 的同步在线玩家 API。 */
public final class ExcellentEconomyProvider extends ReflectiveEconomyProvider {
    private final String currencyId;
    private final Method getCurrency;
    private final Method getBalance;
    private final Method withdraw;
    private final Method deposit;
    private final Method canOperate;
    private final Method isInteger;
    private final Method isUnderLimit;

    public ExcellentEconomyProvider(Plugin plugin, String currencyId) throws ReflectiveOperationException {
        super(plugin);
        this.currencyId = currencyId;
        Class<?> type = api.getClass();
        getCurrency = type.getMethod("getCurrency", String.class);
        getBalance = type.getMethod("getBalance", Player.class, String.class);
        withdraw = type.getMethod("withdraw", Player.class, String.class, double.class);
        deposit = type.getMethod("deposit", Player.class, String.class, double.class);
        canOperate = type.getMethod("canPerformOperations");
        Class<?> currencyType = getCurrency.getReturnType();
        isInteger = currencyType.getMethod("isInteger");
        isUnderLimit = currencyType.getMethod("isUnderLimit", double.class);
        if (currency() == null) throw new IllegalArgumentException("不存在币种: " + currencyId);
    }

    private Object currency() { return invoke(getCurrency, api, currencyId); }
    @Override public String getName() { return plugin.getName() + ":" + currencyId; }
    @Override public boolean isAvailable() {
        return super.isAvailable() && Boolean.TRUE.equals(invoke(canOperate, api)) && currency() != null;
    }
    @Override public boolean supports(BigDecimal amount) {
        if (!super.supports(amount)) return false;
        Object currency = currency();
        return currency != null && (!Boolean.TRUE.equals(invoke(isInteger, currency))
                || amount.stripTrailingZeros().scale() <= 0);
    }
    private double balance(Player player) {
        return ((Number) invoke(getBalance, api, player, currencyId)).doubleValue();
    }
    @Override public boolean has(Player player, BigDecimal amount) {
        return isAvailable() && supports(amount) && balance(player) >= amount.doubleValue();
    }
    @Override public EconomyTransactionResult withdraw(Player player, BigDecimal amount) {
        if (!has(player, amount)) return EconomyTransactionResult.failure("余额不足或币种不可用");
        return result(invoke(withdraw, api, player, currencyId, amount.doubleValue()));
    }
    @Override public EconomyTransactionResult deposit(Player player, BigDecimal amount) {
        if (!isAvailable() || !supports(amount)
                || !Boolean.TRUE.equals(invoke(isUnderLimit, currency(), balance(player) + amount.doubleValue()))) {
            return EconomyTransactionResult.failure("金额无效或收款余额超过币种上限");
        }
        return result(invoke(deposit, api, player, currencyId, amount.doubleValue()));
    }
}
