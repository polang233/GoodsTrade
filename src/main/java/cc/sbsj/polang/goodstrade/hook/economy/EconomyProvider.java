package cc.sbsj.polang.goodstrade.hook.economy;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

/** 可选经济插件的边界，不向交易逻辑暴露第三方类型。 */
public interface EconomyProvider {
    default boolean isAvailable() { return true; }

    default boolean supports(BigDecimal amount) {
        return cc.sbsj.polang.goodstrade.trade.MoneyTrade.isFiniteAmount(amount);
    }

    boolean has(Player player, BigDecimal amount);

    EconomyTransactionResult withdraw(Player player, BigDecimal amount);

    EconomyTransactionResult deposit(Player player, BigDecimal amount);

    String format(BigDecimal amount);

    String getName();
}
