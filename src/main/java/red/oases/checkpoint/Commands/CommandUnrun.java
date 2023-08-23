package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.ProgressUtils;

@DisableConsole
public class CommandUnrun extends Command {
    public CommandUnrun(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足：/cpt unrun <campaign>", sender);
            return true;
        }

        var cam = args[1];

        if (!Campaign.isPresent(cam)) {
            LogUtils.send("竞赛 " + cam + " 不存在。", sender);
            return true;
        }

        var p = (Player) sender;

        var running = ProgressUtils.getRunningCampaign(p);

        if (running == null) {
            LogUtils.send("你还没有准备任何竞赛。", sender);
            return true;
        }

        ProgressUtils.deleteProgress(p);

        LogUtils.send("你已取消准备 " + cam + "。", sender);

        return true;
    }
}
