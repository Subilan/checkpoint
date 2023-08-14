package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.*;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Objects.Point;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.CommonUtils;

@DisableConsole
public class CommandBuild extends Command {
    public CommandBuild(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 3) {
            LogUtils.send("参数不足: /cpt build <track> <number>", sender);
            return true;
        }

        var track = args[1];
        var number = CommonUtils.mustPositive(args[2]);

        if (args[2].contains(".")) {
            LogUtils.send("错误: 序号必须为整数。", sender);
            return true;
        }

        if (number == 0) {
            LogUtils.send("错误: 序号必须为正整数。", sender);
            return true;
        }

        assert sender instanceof Player;
        var playerId = ((Player) sender).getUniqueId().toString();

        if (Selection.getState(playerId) != 2) {
            LogUtils.send("错误: 必须在已选择两对角线顶点的情况下执行该指令。", sender);
            return true;
        }

        if (Point.isPresent(track, number)) {
            LogUtils.send("错误：" + track + "." + number + " 已经存在。", sender);
            return true;
        }

        Point.build(
                track,
                number,
                Selection.getData(playerId, 1),
                Selection.getData(playerId, 2),
                sender.getName()
        );
        Selection.clear(playerId);

        LogUtils.send("成功: 已创建路径点。", sender);
        return true;
    }
}
