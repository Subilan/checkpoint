package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Objects.Point;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.LogUtils;

public class CommandSetcheckpoint extends Command {

    public CommandSetcheckpoint(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        if (args.length < 3) {
            LogUtils.send("参数不足：/cpt setcheckpoint <track> <number> [flag]", sender);
            return true;
        }

        var track = args[1];
        var number = CommonUtils.mustPositive(args[2]);
        var flag = true;

        if (number == 0) {
            LogUtils.send("数字不合法：%d。".formatted(number), sender);
            return true;
        }

        if (!Point.isPresent(track, number)) {
            LogUtils.send("路径点 %s.%d 不存在。".formatted(track, number), sender);
            return true;
        }

        if (args.length >= 4) {
            flag = args[3].equals("true");
        }

        var pt = new Point(track, number);
        pt.setCheckpoint(flag);

        if (flag) LogUtils.send("已将路径点 %s.%d 设置为检查点。".formatted(track, number), sender);
        else LogUtils.send("已取消路径点 %s.%d 的检查点。".formatted(track, number), sender);

        return true;
    }
}
