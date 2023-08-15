package red.oases.checkpoint.Objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Utils.FileUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Analytics {
    public Campaign campaign;
    public String uuid;

    public static @Nullable Analytics of(Campaign c, Player p) {
        var target = FileUtils.analytics.getConfigurationSection(c.getName());
        if (target == null) return null;
        var keys = target.getKeys(false);
        for (var k : keys) {
            if (Objects.equals(target.getString(k + ".player"), p.getName())) {
                return new Analytics(c, k);
            }
        }
        return null;
    }

    public Analytics(Campaign c, String uuid) {
        this.campaign = c;
        this.uuid = uuid;
    }

    public ConfigurationSection getSection() {
        return FileUtils.analytics.getConfigurationSection(this.campaign.getName() + "." + this.uuid);
    }

    public String getUUID() {
        return getSection().getString("result_id");
    }

    public Campaign getCampaign() {
        return this.campaign;
    }

    public String getCampaignName() {
        return getSection().getString("campaign_name");
    }

    public String getTrackName() {
        return getSection().getString("track_name");
    }

    public String getPlayerName() {
        return getSection().getString("player");
    }

    public Date getFinishedAt() {
        return new Date(getSection().getLong("finished_at"));
    }

    public List<Integer> getTimeParts() {
        return getSection().getIntegerList("time_parts");
    }

    public long getTimeTotal() {
        return getSection().getLong("time_total");
    }

    public void destory() {
        FileUtils.analytics.set(this.campaign.getName() + "." + this.uuid, null);
        FileUtils.saveAnalytics();
    }
}
