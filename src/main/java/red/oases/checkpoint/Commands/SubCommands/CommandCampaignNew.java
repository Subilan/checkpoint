package red.oases.checkpoint.Commands.SubCommands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Campaign;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.LogUtil;
import red.oases.checkpoint.Utils;

public class CommandCampaignNew extends Command {
    public CommandCampaignNew(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 3) {
            LogUtil.send("参数不足：/cpt campaign new <name> [track]", sender);
        }

        var name = args[2];
        var track = name;

        if (args.length >= 4) {
            track = args[3];
        }

        var tracks = Utils.getTrackNames();
        var campaigns = Utils.getCampaignNames();

        if (!tracks.contains(track)) {
            LogUtil.send("赛道 " + track + " 不存在。", sender);
            return true;
        }

        if (campaigns.contains(name)) {
            LogUtil.send("竞赛 " + name + " 已存在。", sender);
            return true;
        }

        var campaign = new Campaign(name).create(track, sender);

        LogUtil.send("已创建竞赛 " + name + "。", sender);
        LogUtil.send("竞赛现在处于 " + (campaign.isOpen ? "开启" : "关闭") + " 状态。", sender);
        return true;
    }
}
