package red.oases.checkpoint.Objects;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class PlayerTimer {
    public static MemoryConfiguration timerStorage;
    public static Map<Player, Timer> timers = new HashMap<>();

    public static DedicatedPlayerTimer getDedicated(Player p) {
        return new DedicatedPlayerTimer(p);
    }

    public static String path(Player p, Integer number) {
        return p.getName() + "." + number.toString();
    }

    public static void tick(Player p, int number) {
        timerStorage.set(
                path(p, number),
                timerStorage.getInt(path(p, number)) + 1
        );
    }

    public static Timer getTimer(Player p) {
        return timers.get(p);
    }

    public static void renewTimer(Player p){
        timers.remove(p);
        timers.put(p, new Timer());
    }
}

