package cc.sbsj.polang.goodstrade.hook;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;

public final class VaultEconomyProvider implements EconomyProvider {
    private final Economy economy;

    private VaultEconomyProvider(Economy economy) {
        this.economy = economy;
    }

    public static VaultEconomyProvider hook(GoodsTrade plugin) {
        RegisteredServiceProvider<Economy> registration = plugin.getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (registration == null || registration.getProvider() == null) return null;
        return new VaultEconomyProvider(registration.getProvider());
    }

    @Override
    public boolean has(Player player, BigDecimal amount) {
        return economy.has(player, amount.doubleValue());
    }

    @Override
    public EconomyTransactionResult withdraw(Player player, BigDecimal amount) {
        return convert(economy.withdrawPlayer(player, amount.doubleValue()));
    }

    @Override
    public EconomyTransactionResult deposit(Player player, BigDecimal amount) {
        return convert(economy.depositPlayer(player, amount.doubleValue()));
    }

    @Override
    public String format(BigDecimal amount) {
        return economy.format(amount.doubleValue());
    }

    @Override
    public String getName() {
        return economy.getName();
    }

    private EconomyTransactionResult convert(EconomyResponse response) {
        if (response != null && response.transactionSuccess()) {
            return EconomyTransactionResult.success();
        }
        return EconomyTransactionResult.failure(response == null ? "null response" : response.errorMessage);
    }
}
