package red.oases.checkpoint.Utils;

import org.bukkit.entity.Player;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.Progress;

import java.util.List;
public class ProgressUtils {

    public static List<String> getFinishedPlayers(Campaign campaign) {
        var section = FileUtils.progress.getConfigurationSection("campaign_finished");
        if (section == null) return List.of();
        return section.getKeys(false).stream().filter(p ->
                FileUtils.progress.getStringList("campaign_finished." + p)
                .contains(campaign.getName())
        ).toList();
    }

    public static boolean isHalfway(Player p) {
        return getCursor(p) > 0;
    }

    /**
     * 返回当前玩家所在的锚点，指向上一个通过的点的序号
     * （0 表示还没有到第一个点）
     *
     * @param p 玩家
     * @return 锚点数值
     */
    public static Integer getCursor(Player p) {
        var pt = Progress.getPoint(p);
        if (pt == null) return 0;
        else return pt.number;
    }
}
