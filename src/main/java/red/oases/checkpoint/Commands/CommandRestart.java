package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.PlayerTimer;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.LogUtils;

@PermissionLevel(0)
@DisableConsole
public class CommandRestart extends Command {
    public CommandRestart(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        var campaign = Campaign.of(sender.getName());

        var p = (Player) sender;

        if (campaign == null) {
            LogUtils.send("你还没有加入任何竞赛。", sender);
            return true;
        }

        if (!campaign.isFinished(p) && PlayerTimer.getTicks(p).isEmpty()) {
            LogUtils.send("你还没有开始比赛。", sender);
            return true;
        }

        CommonUtils.cleanCampaignFor(p);
        LogUtils.send("你已重置比赛状态。", sender);
        return true;
    }
}
