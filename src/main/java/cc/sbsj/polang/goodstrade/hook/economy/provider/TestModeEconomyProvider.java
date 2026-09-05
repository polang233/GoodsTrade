package cc.sbsj.polang.goodstrade.hook.economy.provider;

import cc.sbsj.polang.goodstrade.hook.economy.EconomyProvider;
import cc.sbsj.polang.goodstrade.hook.economy.EconomyTransactionResult;

import org.bukkit.entity.Player;
import java.math.BigDecimal;

/** 未安装经济插件时，管理员仍可演练金额按钮。 */
public final class TestModeEconomyProvider implements EconomyProvider {
    @Override public boolean isAvailable() { return false; }
    @Override public boolean has(Player player, BigDecimal amount) { return false; }
    @Override public EconomyTransactionResult withdraw(Player player, BigDecimal amount) {
        return EconomyTransactionResult.failure("测试模式禁止扣款");
    }
    @Override public EconomyTransactionResult deposit(Player player, BigDecimal amount) {
        return EconomyTransactionResult.failure("测试模式禁止入账");
    }
    @Override public String format(BigDecimal amount) { return amount.stripTrailingZeros().toPlainString(); }
    @Override public String getName() { return "Test mode"; }
}
