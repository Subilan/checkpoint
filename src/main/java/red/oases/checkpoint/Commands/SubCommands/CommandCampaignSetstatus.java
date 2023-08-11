package red.oases.checkpoint.Commands.SubCommands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Campaign;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.LogUtil;
import red.oases.checkpoint.Utils;

public class CommandCampaignSetstatus extends Command {
    public CommandCampaignSetstatus(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 4) {
            LogUtil.send("参数不足：/cpt setstatus <name> <state>", sender);
        }

        var name = args[2];
        var status = args[3];
        var campaigns = Utils.getCampaignNames();

        if (!campaigns.contains(name)) {
            LogUtil.send("竞赛 " + name + " 不存在。", sender);
            return true;
        }

        if (!status.equals("open") && !status.equals("close")) {
            LogUtil.send("状态必须为 open 或者 close。", sender);
            return true;
        }

        var campaign = new Campaign(name);

        campaign.setStatus(status);

        LogUtil.send("已将竞赛 " + name + " 的状态设置为 " + status + "。", sender);

        return true;
    }
}
