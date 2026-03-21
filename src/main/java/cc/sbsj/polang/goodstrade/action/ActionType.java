package cc.sbsj.polang.goodstrade.action;

import cc.sbsj.polang.goodstrade.manager.ConfigManager;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pers.neige.neigeitems.action.ActionContext;
import pers.neige.neigeitems.action.ActionResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Getter
public enum ActionType {
    RELOAD,
    BLACKLIST_ITEM,
    DROP_ITEM,
    OFFLINE_PLAYER,
    INVALID_SENDER,
    TRADE_SELF,
    TRADE_SAME,
    NO_REQUEST,
    REQUEST_PLAYER_OFFLINE,
    YOU_CANCEL_TRADE,
    TARGET_CANCEL_TRADE,
    OPENING_TRADE_GUI,
    REQUEST_COOLDOWN,
    REQUEST_SENT,
    CANCEL_BY_RELOAD,
    NO_SUCH_REQUEST,
    ;

    private final String configKey = name().toLowerCase().replace('_', '-');

    public @Nullable CompletableFuture<ActionResult> eval(@NonNull ActionContext context) {
        val action = ConfigManager.getActions().get(configKey);
        if (action == null) return null;
        return action.eval(context);
    }

    public @Nullable CompletableFuture<ActionResult> eval(@Nullable CommandSender source) {
        if (source instanceof Player) {
            return eval(new ActionContext((Player) source));
        } else {
            return eval(new ActionContext(null));
        }
    }

    public @Nullable CompletableFuture<ActionResult> eval(@Nullable CommandSender source, @NonNull Map<String, Object> paramsAndGlobal) {
        if (source instanceof Player) {
            return eval(new ActionContext((Player) source, paramsAndGlobal, paramsAndGlobal));
        } else {
            return eval(new ActionContext(null, paramsAndGlobal, paramsAndGlobal));
        }
    }
}
