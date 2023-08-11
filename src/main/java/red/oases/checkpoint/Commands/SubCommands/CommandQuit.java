package red.oases.checkpoint.Commands.SubCommands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Annotations.DisableConsole;
import red.oases.checkpoint.Annotations.PermissionLevel;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.LogUtil;
import red.oases.checkpoint.Utils;

@PermissionLevel(0)
@DisableConsole
public class CommandQuit extends Command {
    public CommandQuit(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        var campaign = Utils.getCampaignOfPlayer(sender.getName());

        if (campaign == null) {
            LogUtil.send("你还没有加入任何竞赛。", sender);
            return true;
        }

        campaign.removePlayer(sender.getName());
        LogUtil.send("你已退出竞赛 " + campaign.name + "。", sender);
        return true;
    }
}
