package red.oases.checkpoint;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import red.oases.checkpoint.Objects.PlayerTimer;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.CommonUtils;

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

        if (!p.isGliding()) return;

        var campaign = CommonUtils.getCampaignOfPlayer(p.getName());

        if (campaign == null) return;

        var loc = p.getLocation();
        var x = loc.getBlockX();
        var y = loc.getBlockY();
        var z = loc.getBlockZ();

        var points = campaign.getTrack().getPoints();
        for (var pt : points) {
            if (pt.covers(x, y, z)) {
                if (pt.isLast()) {
                    assert pt.getPrevious() != null;
                    PlayerTimer.getDedicated(p).stopTimerFor(p, pt.getPrevious());
                    campaign.setFinished(PlayerTimer.getDedicated(p));
                    break;
                } else {
                    if (pt.hasPrevious()) {
                        assert pt.getPrevious() != null;
                        PlayerTimer.getDedicated(p).stopTimerFor(p, pt.getPrevious());
                    }
                    PlayerTimer.getDedicated(p).startTimerFor(p, pt);
                }
            }
        }
    }
}
