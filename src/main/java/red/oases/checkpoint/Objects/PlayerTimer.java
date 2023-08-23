package red.oases.checkpoint.Objects;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Utils.CommonUtils;

import java.util.*;

public class PlayerTimer {
    public static MemoryConfiguration timerStorage = new MemoryConfiguration();
    public static Map<Player, Timer> timers = new HashMap<>();

    public static DedicatedPlayerTimer getDedicated(Player p) {
        return new DedicatedPlayerTimer(p);
    }

    public static String path(Player p, Campaign campaign, Integer number) {
        return p.getName() + "." + campaign.getName() + "." + number.toString();
    }

    public static void tick(Player p, Campaign campaign, int number) {
        timerStorage.set(
                path(p, campaign, number),
                timerStorage.getLong(path(p, campaign, number)) + 1
        );
    }

    public static Long getTick(Player p, Campaign campaign, Integer index) {
        return timerStorage.getLong(path(p, campaign, index));
    }

    public static Long getTotalTime(Player p, Campaign campaign) {
        var ticks = getTicks(p, campaign);
        var sum = 0;
        for (var t : ticks) sum += t;
        return (long) sum;
    }

    public static List<Long> getTicks(Player p, Campaign campaign) {
        var section = timerStorage.getConfigurationSection(
                p.getName() + "." + campaign.getName()
        );
        if (section == null) return List.of();
        var list = new ArrayList<Long>();
        for (var k : section.getKeys(false)) {
            var i = CommonUtils.mustPositive(k);
            if (i == 0) continue;
            list.add(section.getLong(k));
        }
        return list.stream().toList();
    }

    public static Timer getTimer(Player p) {
        return timers.get(p);
    }

    public static void renewTimer(Player p) {
        if (timers.containsKey(p)) {
            timers.get(p).cancel();
        }
        timers.remove(p);
        timers.put(p, new Timer());
    }

    public static void reset(Player p) {
        renewTimer(p);
        timerStorage.set(p.getName(), null);
    }
}

