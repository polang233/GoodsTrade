package cc.sbsj.polang.goodstrade.hook;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

/** Keeps Vault classes out of the main plugin class so item-only trading still loads without Vault. */
public interface EconomyProvider {
    boolean has(Player player, BigDecimal amount);

    EconomyTransactionResult withdraw(Player player, BigDecimal amount);

    EconomyTransactionResult deposit(Player player, BigDecimal amount);

    String format(BigDecimal amount);

    String getName();
}
