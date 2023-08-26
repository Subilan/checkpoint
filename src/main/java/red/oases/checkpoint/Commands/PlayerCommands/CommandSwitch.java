package red.oases.checkpoint.Commands.PlayerCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.Config;
import red.oases.checkpoint.Objects.Logic;
import red.oases.checkpoint.Objects.Progress;
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

        if (!Progress.isCampaignEnabled(p)) {
            LogUtils.send("你还没有参赛。", sender);
            LogUtils.send("请使用 /cpt join 来参赛。", sender);
            return true;
        }

        var cam = args[1];

        if (!Campaign.isPresent(cam)) {
            LogUtils.send(cam + " 对应的竞赛不存在", sender);
            return true;
        }

        var currentCampaign = Progress.getRunningCampaign(p);
        var campaign = new Campaign(cam);

        if (ProgressUtils.isHalfway(p)) {
            LogUtils.send("切换失败：当前比赛尚未完成。", sender);
            LogUtils.send("如果确实需要切换，请使用 /cpt reset " + campaign.getName() + " 清空当前比赛数据再切换。", sender);
            return true;
        }

        if (Progress.isFinished(p, campaign)) {
            LogUtils.send("提示：你已完成 "+ campaign.getName() + "。", sender);
            LogUtils.send("如需重新开始，请输入 /cpt reset " + campaign.getName() + " 清除数据。", sender);
        }

        if (currentCampaign != null) {
            if (currentCampaign.getName().equals(cam)) {
                LogUtils.send("你已经在竞赛 " + campaign.getName() + " 中了。", sender);
                return true;
            }

            if (Config.getBoolean("single-choice")) {
                Logic.cleanCampaignRecord(p, currentCampaign, true);
                LogUtils.send("你在 " + currentCampaign.getName() + " 中的数据已删除。", sender);
            }
        }

        if (Logic.join(p, campaign)) {
            LogUtils.send("已切换到 " + campaign.getName() + "。", sender);
        }

        return true;
    }
}
