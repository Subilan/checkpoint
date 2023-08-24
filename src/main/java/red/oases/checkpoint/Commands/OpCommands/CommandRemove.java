package red.oases.checkpoint.Commands.OpCommands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Objects.Point;
import red.oases.checkpoint.Utils.CommonUtils;

public class CommandRemove extends Command {

    public CommandRemove(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 3) {
            LogUtils.send("参数不足：/cpt remove <track> <number>", sender);
            return true;
        }

        var track = args[1];
        var number = CommonUtils.mustPositive(args[2]);
        var path = String.format("%s.%s", track, number);

        if (number == 0) {
            LogUtils.send("数字不合法：" + number+"。", sender);
            return true;
        }

        if (!Point.isPresent(track, number)) {
            LogUtils.send("路径点 " + path + " 不存在。", sender);
            return true;
        }

        Point.delete(track, number);
        LogUtils.send("成功删除路径点 " + path + "。", sender);

        return true;
    }
}
