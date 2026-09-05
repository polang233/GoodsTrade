package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.gui.view.TradeView;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import cc.sbsj.polang.goodstrade.GoodsTrade;
import cc.sbsj.polang.goodstrade.hook.economy.TradeCurrency;
import cc.sbsj.polang.goodstrade.hook.economy.provider.TestModeEconomyProvider;

@Getter
@Setter
//交易状态类，记录当前交易的双方及进度
public class TradeSession {
    private Player senderPlayer;
    private Player targetPlayer;
    private boolean senderReady;        // 玩家 1 是否确认
    private boolean targetReady;        // 玩家 2 是否确认
    private boolean isConfirmed;         // 交易是否完成
    private final List<TradeCurrency> currencies;
    @Getter(lombok.AccessLevel.NONE)
    private final Map<TradeCurrency, BigDecimal> senderOffers = new LinkedHashMap<>();
    @Getter(lombok.AccessLevel.NONE)
    private final Map<TradeCurrency, BigDecimal> targetOffers = new LinkedHashMap<>();
    @Setter(lombok.AccessLevel.NONE)
    private int currencyIndex;
    private boolean testMode;            // 管理员单人控制双方的测试交易
    private String targetDisplayName;
    private TradeView view;

    public TradeSession(Player senderPlayer, Player targetPlayer, TradeView view) {
        this(senderPlayer, targetPlayer, view, GoodsTrade.currencies);
    }

    private TradeSession(Player senderPlayer, Player targetPlayer, TradeView view, List<TradeCurrency> currencies) {
        this.senderPlayer = senderPlayer;
        this.targetPlayer = targetPlayer;
        this.senderReady = false;
        this.targetReady = false;
        this.isConfirmed = false;
        this.currencies = Collections.unmodifiableList(new ArrayList<>(currencies));
        this.testMode = false;
        this.targetDisplayName = targetPlayer.getName();
        this.view = view;
    }

    public static TradeSession createTest(Player administrator, String virtualPlayerName, TradeView view) {
        List<TradeCurrency> currencies = GoodsTrade.currencies;
        if (currencies.isEmpty()) {
            currencies = Collections.singletonList(new TradeCurrency("test", "",
                    new TestModeEconomyProvider(),
                    GoodsTrade.config.getEconomyButtonAmounts()));
        }
        TradeSession session = new TradeSession(administrator, administrator, view, currencies);
        session.testMode = true;
        session.targetDisplayName = virtualPlayerName;
        return session;
    }

    public TradeCurrency getCurrency() {
        return currencies.isEmpty() ? null : currencies.get(currencyIndex);
    }

    public void nextCurrency() {
        if (!currencies.isEmpty()) currencyIndex = (currencyIndex + 1) % currencies.size();
    }

    public BigDecimal getOffer(TradeCurrency currency, boolean sender) {
        return (sender ? senderOffers : targetOffers).getOrDefault(currency, BigDecimal.ZERO);
    }

    public BigDecimal getSenderMoney() { return getOffer(getCurrency(), true); }
    public BigDecimal getTargetMoney() { return getOffer(getCurrency(), false); }
    public void setSenderMoney(BigDecimal amount) { senderOffers.put(getCurrency(), amount); }
    public void setTargetMoney(BigDecimal amount) { targetOffers.put(getCurrency(), amount); }

    public boolean bothReady() {
        return senderReady && targetReady;
    }

    public boolean isPlayerSender(Player player) {
        return player.equals(senderPlayer);
    }

    public boolean isPlayerTarget(Player player) {
        return player.equals(targetPlayer);
    }

    public boolean canControlSide(Player player, boolean senderSide) {
        if (testMode) return senderPlayer.equals(player);
        return senderSide ? isPlayerSender(player) : isPlayerTarget(player);
    }

    public String getSenderDisplayName() {
        return senderPlayer.getName();
    }
}
