package red.oases.checkpoint.Objects;

import org.bukkit.entity.Player;

public class Path {
    public static String halfway(Player p, String key) {
        return "halfway." + p.getName() + "." + key;
    }

    public static String paused(Player p, Campaign campaign) {
        return "paused." + p.getName() + "." + campaign.getName();
    }

    public static String timer(Player p, Campaign campaign, Integer number) {
        return p.getName() + "." + campaign.getName() + "." + number.toString();
    }

    public static String ticks(Player p, Campaign campaign) {
        return p.getName() + "." + campaign.getName();
    }

    public static String lastTick(Player p) {
        return p.getName() + ".last_tick";
    }
}
