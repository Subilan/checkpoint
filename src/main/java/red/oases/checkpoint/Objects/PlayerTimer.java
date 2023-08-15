package red.oases.checkpoint.Objects;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Utils.CommonUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class PlayerTimer {
    public static MemoryConfiguration timerStorage = new MemoryConfiguration();
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
                timerStorage.getLong(path(p, number)) + 1
        );
    }

    public static Long getTick(Player p, Integer index) {
        return timerStorage.getLong(path(p, index));
    }

    public static Long getTotalTime(Player p) {
        var ticks = getTicks(p);
        var sum = 0;
        for (var t : ticks) sum += t;
        return (long) sum;
    }

    public static List<Long> getTicks(Player p) {
        var section = timerStorage.getConfigurationSection(p.getName());
        if (section == null) return List.of();
        var list = new ArrayList<Long>();
        for (var k : section.getKeys(false)) {
            var i = CommonUtils.mustPositive(k);
            if (i == 0) continue;
            list.add(section.getLong(k));
        }
        return list.stream().toList();
    }

    public static Integer getPlayerStage(Player p) {
        return getTicks(p).size();
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

    /**
     * 将 result 转换为两位小数
     * 例如 result==1200 时，返回值为 1.20
     *
     * @param result getTick 结果
     * @return 两位小数
     */
    public static double getTickInSeconds(long result) {
        return Double.parseDouble(String.format("%.2f", (double) result / 1000));
    }

    /**
     * 将 result 转换为人类可读
     * 例如 result==120010（120.01 秒） 时，返回值为 2 分 0.01 秒
     *
     * @param result getTick 结果
     * @return 人类可读
     */
    public static String getTickInReadable(long result) {
        var target = getTickInSeconds(result) / 60;
        var minutes = BigDecimal.valueOf(target).setScale(0, RoundingMode.DOWN).intValue();
        var seconds = String.format("%.2f", (target - (double) minutes) * 60);
        if (minutes > 0) {
            return String.format("%s 分 %s 秒", minutes, seconds);
        } else {
            return String.format("%s 秒", seconds);
        }
    }
}

