package red.oases.checkpoint.Commands.PlayerCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Extra.Exceptions.NoCandidateException;
import red.oases.checkpoint.Objects.Logic;
import red.oases.checkpoint.Objects.Progress;
import red.oases.checkpoint.Utils.LogUtils;

@PermissionLevel(0)
@DisableConsole
public class CommandJoin extends Command {
    public CommandJoin(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        var p = (Player) sender;

        if (Progress.isCampaignEnabled(p)) {
            LogUtils.send("你已参赛。", sender);
            return true;
        }

        String join;

        try {
            join = Logic.joinOrRandom(p);
        } catch (NoCandidateException e) {
            LogUtils.send("参赛失败，暂无比赛可选择。", sender);
            return true;
        }

        LogUtils.send("你已成功参赛。", sender);
        LogUtils.send("默认为你选择的竞赛为 " + join + "。", sender);
        LogUtils.send("如需切换，请使用 /cpt switch 指令。", sender);

        return true;
    }
}
