package red.oases.checkpoint.Utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Extra.Exceptions.ObjectNotFoundException;
import red.oases.checkpoint.Objects.Analytics;
import red.oases.checkpoint.Objects.PlayerTimer;

import java.util.Date;
import java.util.UUID;

public class AnalyticUtils {
    public static void saveCampaignResult(Player p) {
        var campaign = CommonUtils.getCampaignOfPlayer(p);
        if (campaign == null)
            throw new ObjectNotFoundException("Campaign must be present when saving campaign result.");
        if (!isCampaignPresent(campaign.getName())) FileUtils.analytics.createSection(campaign.getName());
        var resultId = UUID.randomUUID().toString();
        var section = FileUtils.analytics.createSection(campaign.getName() + "." + resultId);
        section.set("result_id", resultId);
        section.set("campaign_name", campaign.getName());
        section.set("track_name", campaign.getTrackName());
        section.set("player", p.getName());
        section.set("finished_at", new Date().getTime());
        section.set("time_parts", PlayerTimer.getTicks(p));
        section.set("time_total", PlayerTimer.getTotalTime(p));
        FileUtils.saveAnalytics();
    }

    public static void removeCampaignResult(Player p) {
        var campaign = CommonUtils.getCampaignOfPlayer(p);
        if (campaign == null) return;
        var an = Analytics.of(campaign, p);
        if (an == null) return;
        an.destory();
    }

    public static boolean isCampaignPresent(String campaignName) {
        return FileUtils.analytics.getKeys(false).contains(campaignName);
    }

    public static @Nullable ConfigurationSection getSection(String campaignName) {
        return FileUtils.analytics.getConfigurationSection(campaignName);
    }
}
