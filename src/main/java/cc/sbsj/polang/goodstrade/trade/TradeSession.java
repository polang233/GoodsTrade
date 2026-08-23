package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.gui.view.TradeView;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

@Getter
@Setter
//交易状态类，记录当前交易的双方及进度
public class TradeSession {
    private Player senderPlayer;
    private Player targetPlayer;
    private boolean senderReady;        // 玩家 1 是否确认
    private boolean targetReady;        // 玩家 2 是否确认
    private boolean isConfirmed;         // 交易是否完成
    private BigDecimal senderMoney;      // 发起者设置的金额；负数表示由接收者支付
    private BigDecimal targetMoney;      // 接收者设置的金额；负数表示由发起者支付
    private boolean testMode;            // 管理员单人控制双方的沙盒测试交易
    private String targetDisplayName;
    private TradeView view;

    public TradeSession(Player senderPlayer, Player targetPlayer, TradeView view) {
        this.senderPlayer = senderPlayer;
        this.targetPlayer = targetPlayer;
        this.senderReady = false;
        this.targetReady = false;
        this.isConfirmed = false;
        this.senderMoney = BigDecimal.ZERO;
        this.targetMoney = BigDecimal.ZERO;
        this.testMode = false;
        this.targetDisplayName = targetPlayer.getName();
        this.view = view;
    }

    public static TradeSession createTest(Player administrator, String virtualPlayerName, TradeView view) {
        TradeSession session = new TradeSession(administrator, administrator, view);
        session.testMode = true;
        session.targetDisplayName = virtualPlayerName;
        return session;
    }

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
