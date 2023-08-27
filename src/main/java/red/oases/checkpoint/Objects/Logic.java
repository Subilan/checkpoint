package red.oases.checkpoint.Objects;

import net.kyori.adventure.text.format.NamedTextColor;
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
     * 8. 如果 removeFromCampaign 指定为 true，那么从该竞赛的参赛人中删除该玩家
     * 9. 如果 cleanRunningCampaign 指定为 true，那么删除玩家正在进行的比赛
     *
     * @param p                  玩家
     * @param campaign           指定竞赛
     * @param removeFromCampaign 是否从竞赛参与列表中除去玩家
     */
    public static void cleanCampaignRecord(Player p, Campaign campaign, Boolean removeFromCampaign) {
        var campaigns = Campaign.get(p);
        assert !campaigns.isEmpty() && Campaign.isPresent(campaign.getName());
        LocationLock.unlock(p);
        DedicatedPlayerTimer.unlock(p);
        // 必须放在 campaign 数据被删除之前
        AnalyticUtils.removeCampaignResult(p, campaign);
        PointUtils.clearCheckpoints(p, campaign);
        Progress.unsetFinished(p, campaign);
        Progress.setPaused(p, campaign, false);
        Progress.cleanHalfway(p);
        PlayerTimer.reset(p, campaign);
        if (removeFromCampaign) campaign.removePlayer(p);
    }

    public static void emptyRunningCampaign(Player p) {
        Progress.setRunningCampaign(p, null);
    }

    public static @Nullable String randomCampaignName() {
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

            defaultCam = Logic.randomCampaignName();

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
        Progress.enableCampaignFor(p);
        Progress.setRunningCampaign(p, campaign);
        return true;
    }

    public static void quit(Player p) {
        var campaigns = Campaign.get(p);
        for (var campaign : campaigns) {
            purge(p, campaign);
        }
        Progress.disableCampaignFor(p);
    }

    public static void purge(Player p, Campaign campaign) {
        cleanCampaignRecord(p, campaign, true);
        emptyRunningCampaign(p);
    }

    public static void reset(Player p, Campaign campaign) {
        cleanCampaignRecord(p, campaign, false);
    }

    public static void sendPartialTotal(Player p, Campaign campaign, Point pt) {
        assert pt.getPrevious() != null;
        var totalInSecond = CommonUtils.millisecondsToSeconds(
                PlayerTimer.getTick(p, campaign, pt.getPrevious().number)
        );
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
     *
     * @param p  操作玩家
     * @param pt 当前到达的点
     */
    public static void handleChangePoint(Player p, Campaign campaign, Point pt) {
        var ptm = PlayerTimer.getDedicated(p);
        assert !pt.isLast();

        if (!pt.isFirst()) {
            assert pt.getPrevious() != null;
            ptm.stopTimerFor(p);
            sendPartialTotal(p, campaign, pt);
        }

        ptm.startTimerFor(p, campaign, pt);
        Progress.updatePoint(p, pt);
    }

    /**
     * 处理到达终点时进行的一些操作，包括：
     * 1. 向 campaign 对象的完成玩家列表中添加当前玩家
     * 2. 存储玩家在本次比赛的统计信息
     * 3. 删除所有玩家存在的可传送检查点
     * 4. 删除内存中玩家进度的追踪信息
     * 5. 向玩家发送总用时统计信息
     * 6. 清除内存中的计时数据
     *
     * @param p        操作玩家
     * @param campaign 指定比赛
     */
    public static void handleFinish(Player p, Campaign campaign) {
        Progress.setFinished(p, campaign);
        Progress.cleanHalfway(p);
        Progress.setPaused(p, campaign, false);
        AnalyticUtils.saveCampaignResult(p, campaign);
        PointUtils.clearCheckpoints(p, campaign);
        var total = CommonUtils.millisecondsToReadable(PlayerTimer.getTotalTime(p, campaign));
        // 必须放在 PlayerTimer 调用之后
        PlayerTimer.reset(p, campaign);
        LogUtils.send("你已到达终点，共计用时 " + total + "。", p);
        LogUtils.send("统计数据已存储。", p);
        LogUtils.send("如需清除数据重新开始，键入 /cpt reset " + campaign.getName(), p);
    }

    public static void handleAutoJoin(Player p) {
        String join;
        try {
            join = Logic.joinOrRandom(p);
        } catch (NoCandidateException ex) {
            if (!Config.getDisableWarningAutoJoin()) {
                LogUtils.send("找不到可自动分配比赛，请联系管理员。", p);
            }
            return;
        }
        LogUtils.send("已为你自动分配比赛 " + join + "，开始滑翔吧~", p);
        LogUtils.send("如需切换，请使用 /cpt switch <比赛名称>。", p);
    }

    public static void handleAutoResume(Player p, Boolean warning) {

        if (!ProgressUtils.isHalfway(p)) {
            if (warning) LogUtils.send("继续失败：你已完成或还未开始此比赛。", p);
            return;
        }

        var running = Progress.getRunningCampaign(p);
        assert running != null;

        if (!running.isOpen()) {
            if (warning) LogUtils.send("继续失败：比赛已关闭。", p);
            return;
        }

        if (!Progress.isPaused(p, running)) {
            if (warning) LogUtils.send("继续失败：你没有暂停此比赛。", p);
            return;
        }

        if (Progress.isPauseExpired(p)) {
            if (warning) {
                LogUtils.send("继续失败：数据已过期。", p);
                LogUtils.send("请尝试使用 /cpt reset " + running.getName() + " 重置数据。", p);
            }
            return;
        }

        var pt = Progress.getPoint(p);
        assert pt != null;

        Progress.setPaused(p, running, false);
        if (Config.getDisallowTimerWorkingOffline()) {
            PlayerTimer.retrieveTicks(p, running);
            PlayerTimer.getDedicated(p).startTimerFor(
                    p, running, pt, PlayerTimer.takeLastTick(p)
            );
        }
        SoundUtils.playSoundA(p);
        LogUtils.send("最后通过第 " + pt.number + " 个点", p);
        LogUtils.send(pt.number + "-" + (pt.number + 1) + " 当前用时 " + CommonUtils.millisecondsToSeconds(PlayerTimer.getTick(p, running, pt.number)), p);
        LogUtils.send("当前总计用时 " + CommonUtils.millisecondsToReadable(PlayerTimer.getTotalTime(p, running)), p);

        if (Config.getDisallowTimerWorkingOffline()) {
            LogUtils.sendWithoutPrefix(
                    LogUtils.t("--- ", NamedTextColor.YELLOW)
                            .append(LogUtils.t("比赛 " + running.getName() + " 计时已重新开始", NamedTextColor.GREEN))
                            .append(LogUtils.t(" ---", NamedTextColor.YELLOW)),
                    p
            );
        }
    }
}
