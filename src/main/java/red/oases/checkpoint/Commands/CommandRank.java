package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.DisplayList;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.FileUtils;
import red.oases.checkpoint.Utils.LogUtils;

@PermissionLevel(0)
public class CommandRank extends Command {
    public CommandRank(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足: /cpt rank <campaign> [page]", sender);
            return true;
        }

        var campaign = args[1];
        var page = 1;
        var section = FileUtils.campaigns.getConfigurationSection(campaign);

        if (args.length == 3) {
            page = CommonUtils.mustPositive(args[2]);
            if (page == 0) {
                LogUtils.send("页码无效。", sender);
                return true;
            }
        }

        if (section == null) {
            LogUtils.send(String.format("找不到竞赛 %s", campaign), sender);
            return true;
        }

        var targetCampaign = new Campaign(campaign);

        var analytics = targetCampaign.getAnalytics();
        var list = new DisplayList(
                10,
                analytics.size(),
                sender,
                targetCampaign.getName() + " 的排名信息"
        );

        list.sendPage(page, i -> {
            var targetAnalytics = analytics.get(i);
            return (String.format("[%s] %s - %s - %s\n",
                    i + 1,
                    targetAnalytics.getPlayerName(),
                    CommonUtils.millisecondsToReadable(targetAnalytics.getTimeTotal()),
                    CommonUtils.formatDate(targetAnalytics.getFinishedAt())
            ));
        });

        return true;
    }
}
