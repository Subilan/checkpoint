package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.Config;
import red.oases.checkpoint.Objects.Logic;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.ProgressUtils;


@DisableConsole
@PermissionLevel(0)
public class CommandSwitch extends Command {
    public CommandSwitch(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足：/cpt switch <campaign>", sender);
            return true;
        }

        var p = (Player) sender;

        if (!ProgressUtils.HasCampaignEnabled(p)) {
            LogUtils.send("你还没有参赛。", sender);
            LogUtils.send("请使用 /cpt join 来参赛。", sender);
            return true;
        }

        var cam = args[1];

        if (!Campaign.isPresent(cam)) {
            LogUtils.send(cam + " 对应的竞赛不存在", sender);
            return true;
        }

        var currentCampaign = ProgressUtils.getRunningCampaign(p);
        var campaign = new Campaign(cam);
        if (currentCampaign != null) {
            if (currentCampaign.getName().equals(cam)) {
                LogUtils.send("你已经在竞赛 " + campaign.getName() + " 中了。", sender);
                return true;
            }

            if (Config.getBoolean("single-choice")) {
                Logic.cleanCampaignFor(p, currentCampaign, true);
                LogUtils.send("你在 " + currentCampaign.getName() + " 中的数据已删除。", sender);
            }
        }

        Logic.initializeCampaignFor(p, campaign);

        LogUtils.send("已切换到 " + campaign.getName() + "。", sender);
        return true;
    }
}
