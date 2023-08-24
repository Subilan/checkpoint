package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.Logic;
import red.oases.checkpoint.Objects.PlayerTimer;
import red.oases.checkpoint.Utils.AnalyticUtils;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.ProgressUtils;

@PermissionLevel(0)
@DisableConsole
public class CommandReset extends Command {
    public CommandReset(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足：/cpt reset <campaign>", sender);
            return true;
        }

        var cam = args[1];
        var p = (Player) sender;
        var campaigns = Campaign.get(p);

        if (campaigns.isEmpty()) {
            LogUtils.send("你还没有加入任何竞赛。", sender);
            return true;
        }

        if (!Campaign.isPresent(cam)) {
            LogUtils.send(cam + " 对应竞赛不存在。", sender);
            return true;
        }

        var target = new Campaign(cam);

        if (ProgressUtils.getCursor(p) == 0 && !ProgressUtils.isFinished(p, target)) {
            LogUtils.send("你还没有开始这场比赛。", sender);
            return true;
        }

        Logic.reset(p, target);
        Logic.join(p, target);

        LogUtils.send("你已重置比赛状态。", sender);
        return true;
    }
}
