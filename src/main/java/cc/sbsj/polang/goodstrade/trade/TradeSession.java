package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.gui.view.TradeView;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
//交易状态类
public class TradeSession {
    private Player senderPlayer;
    private Player targetPlayerExact;
    private boolean senderReady;        // 玩家 1 是否确认
    private boolean targetReady;        // 玩家 2 是否确认
    private boolean isConfirmed;         // 交易是否完成
    private TradeView view;

    public TradeSession(Player senderPlayer, Player targetPlayerExact, TradeView view) {
        this.senderPlayer = senderPlayer;
        this.targetPlayerExact = targetPlayerExact;
        this.senderReady = false;
        this.targetReady = false;
        this.isConfirmed = false;
        this.view = view;
    }

    public boolean bothReady() {
        return senderReady && targetReady;
    }

    public boolean isPlayerSender(Player player) {
        return player.equals(senderPlayer);
    }

    public boolean isPlayerTarget(Player player) {
        return player.equals(targetPlayerExact);
    }
}
