package cc.sbsj.polang.goodstrade.colonel.argument.command;

import cc.sbsj.polang.goodstrade.action.ActionType;
import kotlin.Unit;
import lombok.val;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class PlayerArgument extends pers.neige.colonel.arguments.impl.PlayerArgument<CommandSender, Unit> {
    public static final PlayerArgument NONNULL = new PlayerArgument(true);
    public static final PlayerArgument NULLABLE = new PlayerArgument(false);

    private PlayerArgument(boolean nonnull) {
        super(nonnull);
        setNullFailExecutor((context) -> {
            val sender = context.getSource();
            if (sender == null) return;
            val params = new HashMap<String, Object>();
            params.put("name", context.getInput().readString());
            ActionType.OFFLINE_PLAYER.eval(sender, params);
        });
    }
}
