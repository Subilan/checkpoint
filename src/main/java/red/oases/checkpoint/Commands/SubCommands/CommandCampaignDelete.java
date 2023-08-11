package red.oases.checkpoint.Commands.SubCommands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Campaign;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.LogUtil;
import red.oases.checkpoint.Utils;

public class CommandCampaignDelete extends Command {
    public CommandCampaignDelete(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {

        var name = args[2];
        var campaigns = Utils.getCampaignNames();

        if (!campaigns.contains(name)) {
            LogUtil.send("竞赛 " + name + " 不存在。", sender);
            return true;
        }

        new Campaign(name).delete();

        LogUtil.send("已删除竞赛 " + name + "。", sender);

        return true;
    }
}
