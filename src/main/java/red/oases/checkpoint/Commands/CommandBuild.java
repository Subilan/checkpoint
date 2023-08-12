package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.*;
import red.oases.checkpoint.Annotations.DisableConsole;

@DisableConsole
public class CommandBuild extends Command {
    public CommandBuild(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 3) {
            LogUtil.send("参数不足: /cpt build <track> <number>", sender);
            return true;
        }

        var track = args[1];
        var number = Utils.mustPositive(args[2]);

        if (args[2].contains(".")) {
            LogUtil.send("错误: 序号必须为整数。", sender);
            return true;
        }

        if (number == 0) {
            LogUtil.send("错误: 序号必须为正整数。", sender);
            return true;
        }

        assert sender instanceof Player;
        var playerId = ((Player) sender).getUniqueId().toString();

        if (Selection.getState(playerId) != 2) {
            LogUtil.send("错误: 必须在已选择两对角线顶点的情况下执行该指令。", sender);
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

        LogUtil.send("成功: 已创建路径点。", sender);
        return true;
    }
}
