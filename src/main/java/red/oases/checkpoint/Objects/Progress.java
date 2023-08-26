package red.oases.checkpoint.Objects;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Extra.Exceptions.ObjectNotFoundException;
import red.oases.checkpoint.Utils.FileUtils;

import java.util.Date;


public class Progress {

    public static void setRunningCampaign(Player p, @Nullable Campaign campaign) {
        if (campaign != null) FileUtils.progress.set("campaign_running." + p.getName(), campaign.getName());
        else FileUtils.progress.set("campaign_running." + p.getName(), null);
        FileUtils.saveProgress();
    }

    /**
     * 在玩家进度记录中添加该点，并将当前 point 指向最新的点。
     *
     * @param pt 点
     */
    public static void updatePoint(Player p, Point pt) {
        FileUtils.progress.set(Path.halfway(p, "at.track"), pt.track.name);
        FileUtils.progress.set(Path.halfway(p, "at.number"), pt.number);
        FileUtils.progress.set(Path.halfway(p, "last_updated"), new Date().getTime());
        FileUtils.saveProgress();
    }

    /**
     * 将玩家 p 标记为已暂停。
     * @param p 玩家
     */
    public static void setPaused(Player p, Campaign campaign, Boolean flag) {
        FileUtils.progress.set(Path.paused(p, campaign), flag);
        FileUtils.saveProgress();
    }

    public static void updateExpiration(Player p) {
        FileUtils.progress.set(Path.halfway(p, "expiration"),
                new Date().getTime() + (1000L * Config.getHalfwayProgressDeadline())
        );
        FileUtils.saveProgress();
    }

    public static boolean isPaused(Player p, Campaign campaign) {
        return FileUtils.progress.getBoolean(Path.paused(p, campaign));
    }

    public static boolean hasPaused(Player p) {
        var section = FileUtils.progress.getConfigurationSection("paused");
        if (section == null) return false;
        var campaigns = section.getConfigurationSection(p.getName());
        if (campaigns == null) return false;
        for (var cam : campaigns.getKeys(false)) {
            if (campaigns.getBoolean(cam)) return true;
        }
        return false;
    }

    public static boolean isPauseExpired(Player p) {
        return FileUtils.progress.getLong(Path.halfway(p, "expiration")) < new Date().getTime();
    }

    public static void setFinished(Player p, Campaign campaign) {
        var list = FileUtils.progress.getStringList("campaign_finished." + p.getName());
        if (!list.contains(campaign.getName())) list.add(campaign.getName());
        FileUtils.progress.set("campaign_finished." + p.getName(), list);
        FileUtils.saveProgress();
    }

    public static void unsetFinished(Player p, Campaign campaign) {
        var list = FileUtils.progress.getStringList("campaign_finished." + p.getName());
        list.remove(campaign.getName());
        FileUtils.progress.set("campaign_finished." + p.getName(), list);
        FileUtils.saveProgress();
    }

    public static boolean isFinished(Player p, Campaign campaign) {
        var list = FileUtils.progress.getStringList("campaign_finished." + p.getName());
        return list.contains(campaign.getName());
    }

    /**
     * 获取玩家所通过的最近的一个点
     *
     * @return 点对象
     */
    public static @Nullable Point getPoint(Player p) {
        try {
            return new Point(
                    FileUtils.progress.getString(Path.halfway(p, "at.track")),
                    FileUtils.progress.getInt(Path.halfway(p, "at.number"))
            );
        } catch (ObjectNotFoundException e) {
            return null;
        }
    }

    /**
     * 获取玩家当前所在的竞赛
     *
     * @return 竞赛对象
     */
    public static @Nullable Campaign getRunningCampaign(Player p) {
        try {
            return new Campaign(
                    FileUtils.progress.getString("campaign_running." + p.getName())
            );
        } catch (ObjectNotFoundException e) {
            return null;
        }
    }

    public static void disableCampaignFor(Player p) {
        var list = FileUtils.progress.getStringList("campaign_enabled");
        list.remove(p.getName());
        FileUtils.progress.set("campaign_enabled", list);
        FileUtils.saveProgress();
    }

    public static void enableCampaignFor(Player p) {
        var list = FileUtils.progress.getStringList("campaign_enabled");
        if (!list.contains(p.getName())) list.add(p.getName());
        FileUtils.progress.set("campaign_enabled", list);
        FileUtils.saveProgress();
    }

    public static boolean isCampaignEnabled(Player p) {
        return FileUtils.progress.getStringList("campaign_enabled").contains(p.getName());
    }

    public static void cleanHalfway(Player p) {
        FileUtils.progress.set("halfway." + p.getName(), null);
        FileUtils.saveProgress();
    }
}
