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

        var campaigns = CommonUtils.getCampaignNames();

        if (!campaigns.contains(cam)) {
            LogUtils.send("竞赛 " + cam + " 不存在。", sender);
            return true;
        }

        var existingCampaign = Campaign.of(sender.getName());

        if (existingCampaign != null) {
            LogUtils.send("你已经加入了竞赛 " + existingCampaign.getName() + "。", sender);

            if (!existingCampaign.getName().equals(cam)) {
                LogUtils.send("若要加入 " + cam + "，请先退出先前的竞赛。", sender);
            }
            return true;
        }

        var campaign = new Campaign(cam);

        if (campaign.isPrivate() || !campaign.isOpen()) {
            LogUtils.send("无法加入此竞赛。", sender);
            if (campaign.isPrivate()) LogUtils.send("此竞赛为私密。", sender);
            else LogUtils.send("此竞赛已关闭或者没有开始。", sender);
            return true;
        }

        campaign.addPlayer((Player) sender);

        LogUtils.send("你已成功加入竞赛 " + cam + "！", sender);

        return true;
    }
}
