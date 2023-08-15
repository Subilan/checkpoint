package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
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

        var result = new StringBuilder("\n" + campaign + " 排名数据\n\n");
        var analytics = targetCampaign.getAnalytics();
        int iterationRangeStart;
        int iterationRangeEnd;
        var lastPage = (int) Math.ceil(analytics.size() / 10d);

        if (page > lastPage) {
            if (page == 1) {
                LogUtils.send("暂无任何参赛者。", sender);
            } else {
                LogUtils.send("页码过大。", sender);
            }
            return true;
        }

        if (analytics.size() <= 10) {
            iterationRangeStart = 0;
            iterationRangeEnd = analytics.size() - 1;
        } else {
            iterationRangeStart = 10 * (page - 1);
            iterationRangeEnd = Math.min(iterationRangeStart + 9, analytics.size() - 1);
        }

        for (var i = iterationRangeStart; i <= iterationRangeEnd; i++) {
            var targetAnalytics = analytics.get(i);
            result.append(String.format("[%s] %s - %s - %s",
                    i + 1,
                    targetAnalytics.getPlayerName(),
                    CommonUtils.millisecondsToReadable(targetAnalytics.getTimeTotal()),
                    CommonUtils.formatDate(targetAnalytics.getFinishedAt())
                    ));
        }

        result.append(String.format(
                "\n\n第 %s 页 - 共 %s 页",
                page,
                lastPage
        ));

        LogUtils.send(result.toString(), sender);
        return true;
    }
}
