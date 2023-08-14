package red.oases.checkpoint.Objects;

import org.bukkit.entity.Player;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

public class LocationLock {
    public static Map<Player, Map.Entry<Point, Boolean>> locks = new HashMap<>();

    public static void lock(Player p, Point pt) {
        locks.put(p, new SimpleEntry<>(pt, true));
    }

    public static void unlock(Player p) {
        locks.remove(p);
    }

    public static boolean isLocked(Player p) {
        if (!locks.containsKey(p)) return false;
        if (locks.get(p) == null) return false;
        else return locks.get(p).getValue();
    }
}
