package cc.sbsj.polang.goodstrade.trade;

import cc.sbsj.polang.goodstrade.gui.view.TradeView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
@RequiredArgsConstructor
//交易状态类
public class TradeSession {
    private final Player senderPlayer;
    private final Player targetPlayer;
    private final TradeView view;
    private boolean senderReady = false;        // 玩家 1 是否确认
    private boolean targetReady = false;        // 玩家 2 是否确认
    private boolean isConfirmed = false;         // 交易是否完成

    public boolean bothReady() {
        return senderReady && targetReady;
    }

    public boolean isPlayerSender(Player player) {
        return player.equals(senderPlayer);
    }

    public boolean isPlayerTarget(Player player) {
        return player.equals(targetPlayer);
    }
}
