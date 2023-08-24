package red.oases.checkpoint.Commands.OpCommands.SubCommands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.CommonUtils;

public class CommandCampaignDelete extends Command {
    public CommandCampaignDelete(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 3) {
            LogUtils.send("参数不足：/cpt campaign delete <name>", sender);
            return true;
        }

        var name = args[2];
        var campaigns = CommonUtils.getCampaignNames();

        if (!campaigns.contains(name)) {
            LogUtils.send("竞赛 " + name + " 不存在。", sender);
            return true;
        }

        new Campaign(name).delete();

        LogUtils.send("已删除竞赛 " + name + "。", sender);

        return true;
    }
}
