package cc.sbsj.polang.goodstrade.manager;

import cc.sbsj.polang.goodstrade.GoodsTrade;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import pers.neige.neigeitems.annotation.Awake;
import pers.neige.neigeitems.manager.BaseActionManager;

@SuppressWarnings("unused")
public class ActionManager extends BaseActionManager {
    public static ActionManager INSTANCE;

    private ActionManager(@NonNull Plugin plugin) {
        super(plugin);
        loadJSLib("NeigeItems", "JavaScriptLib/lib.js");
    }

    @Awake(lifeCycle = Awake.LifeCycle.LOAD)
    private static void initInstance() {
        INSTANCE = new ActionManager(GoodsTrade.getInstance());
    }
}
