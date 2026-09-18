package cc.sbsj.polang.goodstrade.util;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/** 交易请求与界面操作的反馈音效，经 XSound 映射以兼容 1.12。 */
public final class TradeSounds {
    private TradeSounds() {
    }

    public static void requestSent(Player sender, Player target) {
        play(sender, XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
        play(target, XSound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
    }

    public static void opened(Player player) {
        play(player, XSound.BLOCK_CHEST_OPEN, 0.7f, 1.1f);
    }

    public static void confirmed(Player player) {
        play(player, XSound.UI_BUTTON_CLICK, 1.0f, 1.2f);
    }

    public static void cancelled(Player player) {
        play(player, XSound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.7f);
    }

    public static void denied(Player player) {
        play(player, XSound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    public static void play(Player player, XSound sound, float volume, float pitch) {
        if (player == null) return;
        Sound resolved = sound.get();
        if (resolved == null) return;
        player.playSound(player.getLocation(), resolved, volume, pitch);
    }
}
