package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Files;
import red.oases.checkpoint.LogUtil;
import red.oases.checkpoint.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandInfo extends Command {
    public CommandInfo(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length == 1) {
            LogUtil.send("参数不足: /cpt info <alias> 或者 /cpt info <track.number>", sender);
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

        var pos1 = Files.selections.getIntegerList(path + ".pos1");
        var pos2 = Files.selections.getIntegerList(path + ".pos2");
        var creator = Files.selections.getString(path + ".creator");
        var createdAt = Files.selections.getLong(path + ".created_at");

        var result = String.format(
                """
                                                            
                        ---路径点 %s 的详细信息---
                        顶点 1: (%s, %s, %s)
                        顶点 2: (%s, %s, %s)
                        由 %s 创建于 %s""",
                path,
                pos1.get(0), pos1.get(1), pos1.get(2),
                pos2.get(0), pos2.get(1), pos2.get(2),
                creator, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(createdAt))
        );

        var targetAlias = isAlias ? target : Utils.getAliasByPath(target);

        if (!targetAlias.equalsIgnoreCase("")) {
            result += String.format("\n别名: %s", targetAlias);
        }

        LogUtil.send(result, sender);
        return true;
    }
}
