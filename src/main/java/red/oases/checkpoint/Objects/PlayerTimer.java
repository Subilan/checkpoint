package red.oases.checkpoint.Objects;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.FileUtils;

import java.util.*;

public class PlayerTimer {
    public static MemoryConfiguration timerStorage = new MemoryConfiguration();
    public static Map<Player, Timer> timers = new HashMap<>();

    public static DedicatedPlayerTimer getDedicated(Player p) {
        return new DedicatedPlayerTimer(p);
    }

    public static void tick(Player p, Campaign campaign, int number) {
        timerStorage.set(
                Path.tick(p, campaign, number),
                timerStorage.getLong(Path.tick(p, campaign, number)) + 1
        );
    }

    public static Long getTick(Player p, Campaign campaign, Integer number) {
        return timerStorage.getLong(Path.tick(p, campaign, number));
    }

    public static void saveLastTick(Player p) {
        var pt = Progress.getPoint(p);
        var running = Progress.getRunningCampaign(p);
        assert pt != null;
        assert running != null;
        FileUtils.ticks.set(
                Path.lastTick(p),
                PlayerTimer.getTick(p, running, pt.number)
        );
        FileUtils.saveTicks();
    }

    public static Long takeLastTick(Player p) {
        var result = FileUtils.ticks.getLong(Path.lastTick(p));
        FileUtils.ticks.set(Path.lastTick(p), null);
        FileUtils.saveTicks();
        return result;
    }

    public static void saveTicks(Player p, Campaign campaign) {
        FileUtils.ticks.set(
                Path.ticks(p, campaign),
                timerStorage.getConfigurationSection(
                        Path.ticks(p, campaign)
                )
        );
        timerStorage.set(Path.ticks(p, campaign), null);
        FileUtils.saveTicks();
    }

    public static void retrieveTicks(Player p, Campaign campaign) {
        timerStorage.set(
                Path.ticks(p, campaign),
                FileUtils.ticks.getConfigurationSection(
                        Path.ticks(p, campaign)
                )
        );
        FileUtils.ticks.set(Path.ticks(p, campaign), null);
        FileUtils.saveTicks();
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

    public static void reset(Player p, Campaign campaign) {
        renewTimer(p);
        timerStorage.set(Path.ticks(p, campaign), null);
    }
}

