package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.CommonUtils;

@PermissionLevel(0)
@DisableConsole
public class CommandQuit extends Command {
    public CommandQuit(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足：/cpt quit <campaign>", sender);
            return true;
        }

        var cam = args[1];

        if (!Campaign.isPresent(cam)) {
            LogUtils.send("警告：对应竞赛已经不存在，退出操作仍会进行。", sender);
        }

        var p = (Player) sender;

        var campaigns = Campaign.get(p);

        if (campaigns.isEmpty()) {
            LogUtils.send("你还没有加入任何竞赛。", sender);
            return true;
        }

        var campaign = new Campaign(cam);

        CommonUtils.cleanCampaignFor(p, campaign, true);
        LogUtils.send("你已退出竞赛 " + cam + "。", sender);
        return true;
    }
}
