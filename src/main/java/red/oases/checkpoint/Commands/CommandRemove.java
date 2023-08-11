package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Files;
import red.oases.checkpoint.LogUtil;
import red.oases.checkpoint.Utils;

public class CommandRemove extends Command {

    public CommandRemove(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length == 1) {
            LogUtil.send("参数不足: /cpt remove <alias> 或者 /cpt remove <track.number>", sender);
            return true;
        }

        var target = args[1];
        var isAlias = !target.contains(".");
        String path;

        if (isAlias) {
            path = Utils.getPathByAlias(target);
            if (path == null) {
                LogUtil.send(String.format("别名 %s 不存在。", target), sender);
                return true;
            }
        } else {
            path = String.format("data.%s", target);
            if (Files.selections.getConfigurationSection(path) == null) {
                LogUtil.send(String.format("路径点 %s 不存在。", target), sender);
                return true;
            }
        }

        Files.selections.set(path, null);
        Files.saveSelections();

        LogUtil.send(isAlias
                ? String.format("成功删除别名 %s 对应的路径点 %s", target, path)
                : String.format("成功删除路径点 %s", path), sender);

        return true;
    }
}
