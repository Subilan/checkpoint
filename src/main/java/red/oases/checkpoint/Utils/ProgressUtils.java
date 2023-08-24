package red.oases.checkpoint.Utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.Point;
import red.oases.checkpoint.Objects.Progress;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProgressUtils {
    public static Map<Player, Progress> progressStorage = new HashMap<>();

    public static @Nullable Progress getProgress(Player p) {
        return progressStorage.get(p);
    }

    public static @Nullable Campaign getRunningCampaign(Player p) {
        var prog = progressStorage.get(p);
        if (prog == null) return null;
        return prog.getCampaign();
    }

    public static List<String> getFinishedPlayers(Campaign campaign) {
        var section = FileUtils.progress.getConfigurationSection("campaign_finished");
        if (section == null) return List.of();
        return section.getKeys(false).stream().filter(p ->
                FileUtils.progress.getStringList("campaign_finished." + p)
                .contains(campaign.getName())
        ).toList();
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

    public static void setFinished(Player p, Campaign campaign) {
        var list = FileUtils.progress.getStringList("campaign_finished." + p.getName());
        if (!list.contains(campaign.getName())) list.add(campaign.getName());
        FileUtils.progress.set("campaign_finished." + p.getName(), list);
        FileUtils.saveProgress();
    }

    public static void updatePoint(Player p, Point pt) {
        Objects.requireNonNull(getProgress(p)).updatePoint(pt);
    }

    public static void setRunningCampaign(Player p, Campaign campaign) {
        Objects.requireNonNull(getProgress(p)).setCampaign(campaign);
    }

    /**
     * 重置玩家的进度跟踪对象
     * @param p 玩家
     */
    public static void refreshProgress(Player p) {
        progressStorage.remove(p);
        progressStorage.put(p, new Progress(p));
    }
    /**
     * 返回当前玩家所在的锚点，指向上一个通过的点的序号
     * （0 表示还没有到第一个点）
     *
     * @param p 玩家
     * @return 锚点数值
     */
    public static Integer getCursor(Player p) {
        if (getProgress(p) == null) return 0;
        var pt = Objects.requireNonNull(getProgress(p)).getPoint();
        if (pt == null) return 0;
        else return pt.number;
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

    public static boolean HasCampaignEnabled(Player p) {
        return FileUtils.progress.getStringList("campaign_enabled").contains(p.getName());
    }
}
