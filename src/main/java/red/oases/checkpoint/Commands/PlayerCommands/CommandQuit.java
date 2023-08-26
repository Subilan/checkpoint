package red.oases.checkpoint.Commands.PlayerCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Logic;
import red.oases.checkpoint.Objects.Progress;
import red.oases.checkpoint.Utils.LogUtils;

@PermissionLevel(0)
@DisableConsole
public class CommandQuit extends Command {
    public CommandQuit(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        var p = (Player) sender;

        if (!Progress.isCampaignEnabled(p)) {
            LogUtils.send("你还没有参赛。", sender);
            return true;
        }

        Logic.quit(p);

        LogUtils.send("已退出参赛。参赛数据已删除。", sender);
        return true;
    }
}
