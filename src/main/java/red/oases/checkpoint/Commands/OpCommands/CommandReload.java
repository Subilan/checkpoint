package red.oases.checkpoint.Commands.OpCommands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Utils.FileUtils;
import red.oases.checkpoint.Utils.LogUtils;

public class CommandReload extends Command {

    public CommandReload(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        FileUtils.reload();
        LogUtils.send("已刷新内存中的文件。", sender);
        return true;
    }
}
