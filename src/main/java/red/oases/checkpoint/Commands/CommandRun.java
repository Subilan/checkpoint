package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.ProgressUtils;

@DisableConsole
public class CommandRun extends Command {
    public CommandRun(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足：/cpt run <campaign>", sender);
            return true;
        }

        var cam = args[1];

        if (!Campaign.isPresent(cam)) {
            LogUtils.send("竞赛 " + cam + " 不存在。", sender);
            return true;
        }

        var p = (Player) sender;
        var campaign = new Campaign(cam);

        if (campaign.isFinished(p)) {
            LogUtils.send("你已完成该比赛。", sender);
            LogUtils.send("若想清空数据重新开始，请使用 /cpt restart " + campaign.getName(), sender);
            return true;
        }

        var running = ProgressUtils.getRunningCampaign(p);

        if (running != null) {
            LogUtils.send("你已经准备了比赛 " + running.getName() + "。", sender);
            LogUtils.send("如需取消准备，请输入 /cpt unrun " + running.getName(), sender);
            return true;
        }

        ProgressUtils.initProgress(p);
        ProgressUtils.setRunningCampaign(p, campaign);

        LogUtils.send("你已准备 " + cam + "，快快开始滑翔吧！", sender);

        return true;
    }
}
