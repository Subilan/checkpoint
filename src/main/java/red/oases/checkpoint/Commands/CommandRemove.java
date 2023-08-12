package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.LogUtil;
import red.oases.checkpoint.Point;
import red.oases.checkpoint.Utils;

public class CommandRemove extends Command {

    public CommandRemove(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 3) {
            LogUtil.send("参数不足：/cpt remove <track> <number>", sender);
            return true;
        }

        var track = args[1];
        var number = Utils.mustPositive(args[2]);
        var path = String.format("%s.%s", track, number);

        if (number == 0) {
            LogUtil.send("数字不合法：" + number+"。", sender);
            return true;
        }

        if (!Point.isPresent(track, number)) {
            LogUtil.send("路径点 " + path + " 不存在。", sender);
            return true;
        }

        Point.delete(track, number);
        LogUtil.send("成功删除路径点 " + path + "。", sender);

        return true;
    }
}
