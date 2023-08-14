package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.PlayerTimer;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.CommonUtils;

@PermissionLevel(0)
@DisableConsole
public class CommandQuit extends Command {
    public CommandQuit(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        var campaign = CommonUtils.getCampaignOfPlayer(sender.getName());

        if (campaign == null) {
            LogUtils.send("你还没有加入任何竞赛。", sender);
            return true;
        }

        var p = (Player) sender;

        campaign.removePlayer(sender.getName());
        PlayerTimer.reset(p);

        LogUtils.send("你已退出竞赛 " + campaign.getName() + "。", sender);
        return true;
    }
}
