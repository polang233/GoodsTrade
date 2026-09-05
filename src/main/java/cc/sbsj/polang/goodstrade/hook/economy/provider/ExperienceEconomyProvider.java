package cc.sbsj.polang.goodstrade.hook.economy.provider;

import cc.sbsj.polang.goodstrade.hook.economy.EconomyProvider;
import cc.sbsj.polang.goodstrade.hook.economy.EconomyTransactionResult;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

/** 按等级交易，保留经验条进度，不改动累计获得经验的统计。 */
public final class ExperienceEconomyProvider implements EconomyProvider {
    @Override
    public boolean supports(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) return false;
        try {
            amount.intValueExact();
            return true;
        } catch (ArithmeticException ignored) {
            return false;
        }
    }

    @Override
    public boolean has(Player player, BigDecimal amount) {
        return supports(amount) && player.getLevel() >= amount.intValueExact();
    }

    @Override
    public EconomyTransactionResult withdraw(Player player, BigDecimal amount) {
        if (!has(player, amount)) return EconomyTransactionResult.failure("经验等级不足或数量无效");
        player.setLevel(player.getLevel() - amount.intValueExact());
        return EconomyTransactionResult.success();
    }

    @Override
    public EconomyTransactionResult deposit(Player player, BigDecimal amount) {
        if (!supports(amount)) return EconomyTransactionResult.failure("经验等级数量无效");
        long level = (long) player.getLevel() + amount.intValueExact();
        if (level > Integer.MAX_VALUE) return EconomyTransactionResult.failure("经验等级超过上限");
        player.setLevel((int) level);
        return EconomyTransactionResult.success();
    }

    @Override public String format(BigDecimal amount) { return amount.stripTrailingZeros().toPlainString(); }
    @Override public String getName() { return "Minecraft levels"; }
}
