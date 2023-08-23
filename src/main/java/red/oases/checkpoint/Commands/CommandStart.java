package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.ProgressUtils;

@DisableConsole
@PermissionLevel(0)
public class CommandStart extends Command {
    public CommandStart(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足：/cpt start <campaign>", sender);
            return true;
        }

        var cam = args[1];

        if (!Campaign.isPresent(cam)) {
            LogUtils.send("竞赛 " + cam + "不存在。", sender);
            return true;
        }

        var campaign = new Campaign(cam);
        var p = (Player) sender;

        if (!campaign.getPlayers().contains(p.getName())) campaign.addPlayer(p);
        if (ProgressUtils.getProgress(p) != null) LogUtils.send("注意：服务器已有你的进度记录，因而不会重复创建。", sender);
        else ProgressUtils.initProgress(p);
        if (ProgressUtils.getRunningCampaign(p) == campaign) LogUtils.send("注意：你已经准备了 " + campaign.getName() + "，不会重复准备。", sender);
        else ProgressUtils.setRunningCampaign(p, campaign);

        return true;
    }
}
