package red.oases.checkpoint;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import red.oases.checkpoint.Objects.*;
import red.oases.checkpoint.Utils.*;

public class Events implements Listener {

    @EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        var action = e.getAction();
        var block = e.getClickedBlock();
        var player = e.getPlayer();
        var actionId = player.getUniqueId().toString();
        var state = Selection.getState(actionId);

        if (e.getHand() != EquipmentSlot.HAND) return;
        if (block == null) return;
        if (!player.hasPermission("checkpoint.admin")) return;
        if (player.getInventory()
                .getItemInMainHand()
                .getType() != Material.SPECTRAL_ARROW) return;

        e.setCancelled(true);

        if (action.isLeftClick()) {
            if (state == 0) return;
            Selection.clear(actionId);
            LogUtils.send("已清除选择的顶点。", player);
            return;
        }

        var location = block.getLocation();

        Selection.create(
                actionId,
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );

        var afterState = Selection.getState(actionId);

        if (afterState > 0) {
            LogUtils.send(String.format(
                    "已选择顶点 %s (%s, %s, %s)",
                    afterState,
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            ), player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        var p = e.getPlayer();

        if (e.getTo().getBlockX() == e.getFrom().getBlockX()
                && e.getTo().getBlockY() == e.getFrom().getBlockY()
                && e.getTo().getBlockZ() == e.getFrom().getBlockZ()) {
            return;
        }

//        if (!p.isGliding()) return;

        var campaign = ProgressUtils.getRunningCampaign(p);

        if (campaign == null) return;

        var loc = p.getLocation();
        var x = loc.getBlockX();
        var y = loc.getBlockY();
        var z = loc.getBlockZ();

        var points = campaign.getTrack().getPoints();
        Point inPoint = null;
        var stage = ProgressUtils.getCursor(p);
        var nextStage = stage + 1;

        for (var pt : points) {
            if (pt.covers(x, y, z)) {
                if (campaign.isFinished(p)) return;
                inPoint = pt;
                if (LocationLock.isLocked(p)) break;

                if (nextStage != pt.number) {
                    LogUtils.send("你必须先通过第 " + nextStage + " 个检查点。", p);
                    break;
                }

                if (!campaign.isOpen() && !campaign.isPrivate()) {
                    LogUtils.send("比赛已经结束或者未开始。", p);
                    break;
                }

                if (pt.isFirst()) {
                    LogUtils.send("你已通过首个记录点，计时正式开始！", p);
                    SoundUtils.playSoundA(p);
                }

                if (pt.isCheckpoint()) {
                    PointUtils.enableCheckpointFor(p, pt, campaign);
                    SoundUtils.playSoundB(p);
                }

                if (pt.isLast()) {
                    handleFinish(p, campaign);
                    SoundUtils.playSoundC(p);
                    break;
                }

                handleChangePoint(p, campaign, pt);
            }
        }

        if (inPoint != null) {
            LocationLock.lock(p, inPoint);
        } else {
            LocationLock.unlock(p);
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        var p = e.getPlayer();
        var campaigns = Campaign.get(p);
        if (campaigns.isEmpty()) return;
        for (var campaign : campaigns) {
            PointUtils.clearCheckpoints(p, campaign);
        }
        // 当存档机制完善后删除此机制。
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
    public void handleChangePoint(Player p, Campaign campaign, Point pt) {
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
    public void handleFinish(Player p, Campaign campaign) {
        campaign.setFinished(p);
        AnalyticUtils.saveCampaignResult(p, campaign);
        PointUtils.clearCheckpoints(p, campaign);
        ProgressUtils.deleteProgress(p);
        var total = CommonUtils.millisecondsToReadable(PlayerTimer.getTotalTime(p, campaign));
        LogUtils.send("你已到达终点，共计用时 " + total + "。", p);
        LogUtils.send("统计数据已存储。", p);
        LogUtils.send("如需清除数据重新开始，键入 /cpt reset " + campaign.getName(), p);
    }

    public void sendPartialTotal(Player p, Campaign campaign, Point pt) {
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
}
