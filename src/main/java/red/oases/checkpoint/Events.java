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

        var campaign = Campaign.of(p.getName());

        if (campaign == null) return;

        if (campaign.isFinished(p)) return;

        var loc = p.getLocation();
        var x = loc.getBlockX();
        var y = loc.getBlockY();
        var z = loc.getBlockZ();

        var points = campaign.getTrack().getPoints();
        Point inPoint = null;
        var stage = PlayerTimer.getPlayerStage(p);
        var nextStage = stage + 1;

        for (var pt : points) {
            if (pt.covers(x, y, z)) {
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
                    handleChangePoint(p, pt);
                    handleFinish(p, campaign);
                    SoundUtils.playSoundC(p);
                    break;
                } else {
                    if (pt.hasPrevious()) {
                        handleChangePoint(p, pt);
                    }
                    PlayerTimer.getDedicated(p).startTimerFor(p, pt);
                }
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
        var campaign = Campaign.of(p);
        if (campaign == null) return;
        PointUtils.clearCheckpoints(p, campaign);
        // 当存档机制完善后删除此机制。
    }

    public void handleChangePoint(Player p, Point pt) {
        assert pt.getPrevious() != null;
        PlayerTimer.getDedicated(p).stopTimerFor(p, pt.getPrevious());
        sendPartialTotal(p, pt);
    }

    public void handleFinish(Player p, Campaign campaign) {
        campaign.setFinished(p);
        AnalyticUtils.saveCampaignResult(p);
        PointUtils.clearCheckpoints(p, campaign);
        var total = CommonUtils.millisecondsToReadable(PlayerTimer.getTotalTime(p));
        LogUtils.send("你已到达终点，共计用时 " + total + "。", p);
        LogUtils.send("统计数据已存储。", p);
        LogUtils.send("如需清除数据重新开始，键入 /cpt restart。", p);
    }

    public void sendPartialTotal(Player p, Point pt) {
        assert pt.getPrevious() != null;
        var totalInSecond = CommonUtils.millisecondsToSeconds(PlayerTimer.getTick(p, pt.getPrevious().number));
        LogUtils.send(String.format(
                "%s 从 %s 到 %s 共计用时 %s 秒。",
                p.getName(),
                pt.getPrevious().number,
                pt.number,
                totalInSecond
        ), p);
    }
}
