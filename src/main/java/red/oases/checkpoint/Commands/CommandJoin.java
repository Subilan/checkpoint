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
public class CommandJoin extends Command {
    public CommandJoin(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足：/cpt join <竞赛名称>", sender);
            return true;
        }

        var cam = args[1];

        if (!Campaign.isPresent(cam)) {
            LogUtils.send("竞赛 " + cam + " 不存在。", sender);
            return true;
        }

        var p = (Player) sender;

        var existingCampaigns = Campaign.get(p);

        // config
        if (existingCampaigns.size() > 2) {
            LogUtils.send("加入的竞赛数量已达上限。", sender);
            return true;
        }

        for (var c : existingCampaigns) {
            if (c.getName().equals(cam)) {
                LogUtils.send("你已经加入了竞赛 " + c.getName() + "。", sender);
                return true;
            }
        }

        var campaign = new Campaign(cam);

        if (campaign.isPrivate() || !campaign.isOpen()) {
            LogUtils.send("无法加入此竞赛。", sender);
            if (campaign.isPrivate()) LogUtils.send("此竞赛为私密。", sender);
            else LogUtils.send("此竞赛已关闭或者没有开始。", sender);
            return true;
        }

        campaign.addPlayer(p);

        LogUtils.send("你已成功加入竞赛 " + cam + "！", sender);

        return true;
    }
}
