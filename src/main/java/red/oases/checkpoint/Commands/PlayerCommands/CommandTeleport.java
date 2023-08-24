package red.oases.checkpoint.Commands.PlayerCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.PointUtils;
import red.oases.checkpoint.Utils.ProgressUtils;

@DisableConsole
@PermissionLevel(0)
public class CommandTeleport extends Command {

    public CommandTeleport(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足：/cpt tp <number>", sender);
            return true;
        }

        var number = CommonUtils.mustPositive(args[1]);

        if (number == 0) {
            LogUtils.send("传送点序号无效。", sender);
            return true;
        }

        var p = (Player) sender;
        var campaign = ProgressUtils.getRunningCampaign(p);

        if (campaign == null) {
            LogUtils.send("此指令只能在参与竞赛后使用。", sender);
            return true;
        }

        var avail = PointUtils.getAvailableCheckpointsFor(p, campaign);

        if (avail.isEmpty()) {
            LogUtils.send("你没有可以传送的记录点。", sender);
            return true;
        }

        var tg = avail.stream().filter(pt -> pt.number == number).toList();
        assert tg.size() == 1;

        var targetPoint = tg.get(0);

        p.teleport(targetPoint.getTransportableLocation(p.getWorld()));

        LogUtils.send("已传送到记录点 " + number + "。", sender);
        return true;
    }
}
