package red.oases.checkpoint.Utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Extra.Exceptions.DuplicateException;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.Point;
import red.oases.checkpoint.Objects.Progress;

import java.util.HashMap;
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

    public static void updatePoint(Player p, Point pt) {
        Objects.requireNonNull(getProgress(p)).updatePoint(pt);
    }

    public static void setRunningCampaign(Player p, Campaign campaign) {
        Objects.requireNonNull(getProgress(p)).setCampaign(campaign);
    }

    public static void initProgress(Player p) {
        if (progressStorage.containsKey(p)) {
            throw new DuplicateException();
        }
        progressStorage.put(p, new Progress(p));
    }

    public static void refreshProgress(Player p) {
        progressStorage.remove(p);
        progressStorage.put(p, new Progress(p));
    }

    public static void deleteProgress(Player p) {
        progressStorage.remove(p);
    }

    /**
     * 返回当前玩家所在的锚点，指向上一个通过的点的序号
     * （0 表示还没有到第一个点）
     *
     * @param p 玩家
     * @return 锚点数值
     */
    public static Integer getCursor(Player p) {
        var pt = Objects.requireNonNull(getProgress(p)).getPoint();
        if (pt == null) return 0;
        else return pt.number;
    }

    public static void enableCampaignFor(Player p) {
        var list = FileUtils.progress.getStringList("campaign_enabled");
        list.add(p.getName());
        FileUtils.progress.set("campaign_enabled", list);
        FileUtils.saveProgress();
    }

    public static boolean HasCampaignEnabled(Player p) {
        return FileUtils.progress.getStringList("campaign_enabled").contains(p.getName());
    }
}
