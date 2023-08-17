package red.oases.checkpoint.Utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

public class SoundUtils {

    public static final Sound orbPickup = Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.AMBIENT, 1f, 1f);
    public static final Sound levelUp = Sound.sound(Key.key("entity.player.levelup"), Sound.Source.AMBIENT, 1f, 1f);
    public static final Sound twinkle = Sound.sound(Key.key("entity.firework_rocket.twinkle"), Sound.Source.AMBIENT, 2f, 1f);

    public static final Sound blast = Sound.sound(Key.key("entity.firework_rocket.blast"), Sound.Source.AMBIENT, 1f, 1f);

    /**
     * 在玩家 p 周围播放正常通过检查点的声音（吸收经验的声音）
     * @param p 玩家
     */
    public static void playSoundA(Player p) {
        p.playSound(orbPickup);
    }

    /**
     * 在玩家 p 周围播放通过记录点的声音（升级的声音）
     * @param p 玩家
     */
    public static void playSoundB(Player p) {
        p.playSound(levelUp);
    }

    /**
     * 在玩家 p 周围播放到达终点的声音（升级+烟花））
     * @param p 玩家
     */
    public static void playSoundC(Player p) {
        p.playSound(levelUp);
        p.playSound(blast);
        p.playSound(twinkle);
    }
}
