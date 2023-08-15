package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Selection;
import red.oases.checkpoint.Utils.LogUtils;

@DisableConsole
public class CommandStashselection extends Command {
    public CommandStashselection(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        var uuid = ((Player) sender).getUniqueId().toString();
        Selection.clear(uuid);
        LogUtils.send("已清空选区。", sender);
        return true;
    }
}
