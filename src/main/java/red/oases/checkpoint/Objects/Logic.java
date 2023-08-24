package red.oases.checkpoint.Objects;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Extra.Exceptions.NoCandidateException;
import red.oases.checkpoint.Utils.*;

import java.util.Random;

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
        ProgressUtils.refreshProgress(p);
        ProgressUtils.setRunningCampaign(p, null);
        ProgressUtils.unsetFinished(p, campaign);
        PlayerTimer.reset(p);
        if (remove) campaign.removePlayer(p);
    }

    public static @Nullable String randomCampaign() {
        // 只考虑 open 状态的比赛
        var campaigns = CommonUtils.getCampaignNames()
                .stream()
                .filter(cam -> new Campaign(cam).isOpen())
                .toList();
        if (campaigns.isEmpty()) {
            return null;
        }
        return campaigns.get(new Random().nextInt(campaigns.size()));
    }

    /**
     * 使玩家参赛并按照先配置再随机的方式分配一个比赛
     *
     * @param p 操作玩家
     * @return 分配到的比赛。如果没有分配到，为 null
     */
    public static String joinOrRandom(Player p) throws NoCandidateException {
        var defaultCam = Config.getString("default-campaign-name");

        if (defaultCam == null) {

            defaultCam = Logic.randomCampaign();

            if (defaultCam == null) {
                throw new NoCandidateException();
            }
        }

        Logic.join(p, new Campaign(defaultCam));
        return defaultCam;
    }

    public static boolean join(Player p, Campaign campaign) {
        if (!campaign.isOpen()) {
            LogUtils.send(campaign.getName() + " 处于关闭状态，此时无法加入。", p);
            return false;
        }
        campaign.addPlayer(p);
        ProgressUtils.refreshProgress(p);
        ProgressUtils.enableCampaignFor(p);
        ProgressUtils.setRunningCampaign(p, campaign);
        return true;
    }

    public static void quit(Player p) {
        var campaigns = Campaign.get(p);
        for (var campaign : campaigns) {
            cleanCampaignFor(p, campaign, true);
        }
        ProgressUtils.disableCampaignFor(p);
    }

    public static void reset(Player p, Campaign campaign) {
        cleanCampaignFor(p, campaign, false);
    }

    public static void sendPartialTotal(Player p, Campaign campaign, Point pt) {
        assert pt.getPrevious() != null;
        var totalInSecond = CommonUtils.millisecondsToSeconds(PlayerTimer.getTick(p, campaign, pt.getPrevious().number));
        LogUtils.send(String.format(
                "%s 从 %s 到 %s 共计用时 %s 秒。",
                p.getName(),
                pt.getPrevious().number,
                pt.number,
                totalInSecond
        ), p);
    }

    /**
     * 处理切换路径点时进行的一些操作，包括：
     * 1. 如果不是起点，关闭上一个路径点到当前路径点的计时器
     * 2. 如果不是终点，打开当前路径点到下一路径点的计时器
     * 3. 如果不是起点，向玩家发送上一段的计时统计信息
     * 4. 向记录对象中传入当前所到达的点
     * @param p 操作玩家
     * @param pt 当前到达的点
     */
    public static void handleChangePoint(Player p, Campaign campaign, Point pt) {
        if (!pt.isFirst()) {
            assert pt.getPrevious() != null;
            PlayerTimer.getDedicated(p).stopTimerFor(p, pt.getPrevious());
            sendPartialTotal(p, campaign, pt);
        }

        if (!pt.isLast()) {
            PlayerTimer.getDedicated(p).startTimerFor(p, campaign, pt);
        }

        ProgressUtils.updatePoint(p, pt);
    }

    /**
     * 处理到达终点时进行的一些操作，包括：
     * 1. 向 campaign 对象的完成玩家列表中添加当前玩家
     * 2. 存储玩家在本次比赛的统计信息
     * 3. 删除所有玩家存在的可传送检查点
     * 4. 删除内存中玩家进度的追踪信息
     * 5. 向玩家发送总用时统计信息
     * @param p 操作玩家
     * @param campaign 指定比赛
     */
    public static void handleFinish(Player p, Campaign campaign) {
        ProgressUtils.setFinished(p, campaign);
        AnalyticUtils.saveCampaignResult(p, campaign);
        PointUtils.clearCheckpoints(p, campaign);
        ProgressUtils.refreshProgress(p);
        var total = CommonUtils.millisecondsToReadable(PlayerTimer.getTotalTime(p, campaign));
        LogUtils.send("你已到达终点，共计用时 " + total + "。", p);
        LogUtils.send("统计数据已存储。", p);
        LogUtils.send("如需清除数据重新开始，键入 /cpt reset " + campaign.getName(), p);
    }
}
