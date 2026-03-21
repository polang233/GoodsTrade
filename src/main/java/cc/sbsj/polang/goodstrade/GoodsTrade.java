package cc.sbsj.polang.goodstrade;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import pers.neige.neigeitems.scanner.ClassScanner;

public final class GoodsTrade extends JavaPlugin {
    private static GoodsTrade INSTANCE;
    @Getter
    private ClassScanner scanner = null;

    public static GoodsTrade getInstance() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
        scanner = new ClassScanner(this);
        scanner.onLoad();
    }

    @Override
    public void onEnable() {
        scanner.onEnable();
    }

    @Override
    public void onDisable() {
        scanner.onDisable();
    }
}
