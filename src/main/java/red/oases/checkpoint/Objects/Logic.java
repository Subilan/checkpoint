package red.oases.checkpoint.Objects;

import org.bukkit.entity.Player;
import red.oases.checkpoint.Utils.AnalyticUtils;
import red.oases.checkpoint.Utils.PointUtils;
import red.oases.checkpoint.Utils.ProgressUtils;

public class Logic {

    /**
     * 清楚玩家的竞赛数据：
     * 1. 解锁所有位置锁
     * 2. 删除玩家的竞赛统计数据
     * 3. 删除玩家的检查点数据
     * 4. 删除参与中的比赛
     * 5. 重置玩家的进度记录对象
     * 6. 重置玩家的计时器
     * 7. 取消玩家在竞赛中的完成状态
     * 8. 如果 remove 指定为 true，那么从该竞赛的参赛人中删除该玩家
     * @param p 玩家
     * @param campaign 指定竞赛
     * @param remove 是否除名该玩家
     */
    public static void cleanCampaignFor(Player p, Campaign campaign, Boolean remove) {
        var campaigns = Campaign.get(p);
        assert !campaigns.isEmpty() && Campaign.isPresent(campaign.getName());
        LocationLock.unlock(p);
        // 必须放在 campaign 数据被删除之前
        AnalyticUtils.removeCampaignResult(p, campaign);
        PointUtils.clearCheckpoints(p, campaign);
        ProgressUtils.setRunningCampaign(p, null);
        ProgressUtils.refreshProgress(p);
        PlayerTimer.reset(p);
        campaign.unsetFinished(p);
        if (remove) campaign.removePlayer(p);
    }

    public static void initializeCampaignFor(Player p, Campaign campaign) {
        campaign.addPlayer(p);
        ProgressUtils.refreshProgress(p);
        ProgressUtils.setRunningCampaign(p, campaign);
    }
}
