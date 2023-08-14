package red.oases.checkpoint.Commands.SubCommands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.CommonUtils;

public class CommandCampaignNew extends Command {
    public CommandCampaignNew(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 3) {
            LogUtils.send("参数不足：/cpt campaign new <name> [track]", sender);
        }

        var name = args[2];
        var track = name;

        if (args.length >= 4) {
            track = args[3];
        }

        var tracks = CommonUtils.getTrackNames();
        var campaigns = CommonUtils.getCampaignNames();

        if (args.length < 4) {
            LogUtils.send("由于没有指定赛道名称，默认使用与竞赛名称同名赛道。", sender);
        }

        if (!tracks.contains(track)) {
            LogUtils.send("赛道 " + track + " 不存在。", sender);
            return true;
        }

        if (campaigns.contains(name)) {
            LogUtils.send("竞赛 " + name + " 已存在。", sender);
            return true;
        }

        var campaign = Campaign.create(name, track, sender);

        LogUtils.send("已创建竞赛 " + name + "。", sender);
        LogUtils.send("竞赛现在处于 " + (campaign.isOpen() ? "开启" : "关闭") + " 状态。", sender);
        return true;
    }
}
