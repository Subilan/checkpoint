package red.oases.checkpoint;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
        if (!player.hasPermission("checkpoint.admin")) {
            LogUtils.send("你需要拥有 checkpoint.admin 权限才能执行此操作。", player);
            return;
        }
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
                if (ProgressUtils.isFinished(p, campaign)) {
                    SoundUtils.playSoundD(p);
                    LogUtils.send("你已完成这场比赛！", p);
                    LogUtils.send("如需重新开始，请输入 /cpt reset " + campaign.getName() + " 来清除数据。", p);
                    return;
                }
                inPoint = pt;
                if (LocationLock.isLocked(p)) break;

                if (nextStage != pt.number) {
                    SoundUtils.playSoundD(p);
                    LogUtils.send("你必须先通过第 " + nextStage + " 个检查点。", p);
                    break;
                }

                if (!campaign.isOpen() && !campaign.isPrivate()) {
                    LogUtils.send("比赛已经结束或者未开始。", p);
                    break;
                }

                if (pt.isFirst()) {
                    LogUtils.send("你已通过首个记录点，计时正式开始！", p);
                }

                if (pt.isCheckpoint()) {
                    PointUtils.enableCheckpointFor(p, pt, campaign);
                    SoundUtils.playSoundB(p);
                }

                if (pt.isLast()) {
                    Logic.handleFinish(p, campaign);
                    SoundUtils.playSoundC(p);
                    break;
                }

                if (!pt.isLast() && !pt.isCheckpoint()) {
                    SoundUtils.playSoundA(p);
                }

                Logic.handleChangePoint(p, campaign, pt);
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!Config.getBoolean("auto-join-on-login")) return;
        var p = e.getPlayer();
        if (ProgressUtils.HasCampaignEnabled(p)) return;
        var join = Logic.joinOrRandom(p);
        LogUtils.send("已为你自动分配比赛 " + join + "，开始滑翔吧~", p);
        LogUtils.send("如需切换，请使用 /cpt switch <比赛名称>。", p);
    }
}
