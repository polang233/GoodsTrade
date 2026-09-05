package cc.sbsj.polang.goodstrade.hook.economy.provider;

import cc.sbsj.polang.goodstrade.hook.economy.EconomyTransactionResult;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.UUID;

public final class PlayerPointsEconomyProvider extends ReflectiveEconomyProvider {
    private final Method look;
    private final Method take;
    private final Method give;

    public PlayerPointsEconomyProvider(Plugin plugin) throws ReflectiveOperationException {
        super(plugin);
        look = api.getClass().getMethod("look", UUID.class);
        take = api.getClass().getMethod("take", UUID.class, int.class);
        give = api.getClass().getMethod("give", UUID.class, int.class);
    }

    @Override public boolean supports(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) return false;
        try { amount.intValueExact(); return true; }
        catch (ArithmeticException ignored) { return false; }
    }

    @Override public boolean has(Player player, BigDecimal amount) {
        return supports(amount) && balance(player) >= amount.intValueExact();
    }

    private int balance(Player player) {
        return ((Number) invoke(look, api, player.getUniqueId())).intValue();
    }

    @Override public EconomyTransactionResult withdraw(Player player, BigDecimal amount) {
        if (!has(player, amount)) return EconomyTransactionResult.failure("点数不足或金额无效");
        return result(invoke(take, api, player.getUniqueId(), amount.intValueExact()));
    }

    @Override public EconomyTransactionResult deposit(Player player, BigDecimal amount) {
        if (!supports(amount) || (long) balance(player) + amount.intValueExact() > Integer.MAX_VALUE) {
            return EconomyTransactionResult.failure("点数超过整数上限");
        }
        return result(invoke(give, api, player.getUniqueId(), amount.intValueExact()));
    }
}
