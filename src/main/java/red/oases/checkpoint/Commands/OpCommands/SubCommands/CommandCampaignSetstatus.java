package red.oases.checkpoint.Commands.OpCommands.SubCommands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.CommonUtils;

public class CommandCampaignSetstatus extends Command {
    public CommandCampaignSetstatus(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 4) {
            LogUtils.send("参数不足：/cpt setstatus <name> <state>", sender);
        }

        var name = args[2];
        var status = args[3];
        var campaigns = CommonUtils.getCampaignNames();

        if (!campaigns.contains(name)) {
            LogUtils.send("竞赛 " + name + " 不存在。", sender);
            return true;
        }

        if (!status.equals("open") && !status.equals("close") && !status.equals("private")) {
            LogUtils.send("状态必须为 open、close 或者 private。", sender);
            return true;
        }

        var campaign = new Campaign(name);

        campaign.setStatus(status);

        LogUtils.send("已将竞赛 " + name + " 的状态设置为 " + status + "。", sender);

        return true;
    }
}
