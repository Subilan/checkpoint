package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Files;
import red.oases.checkpoint.LogUtil;

import java.util.Date;

public class CommandCopy extends Command {

    public CommandCopy(String[] args, CommandSender sender) {
        super(args, sender);
    }

    public boolean collect() {
        if (args.length < 3) {
            LogUtil.send("参数不足：/cpt copy <from-track.number> <to-track.number> [force?]", sender);
            return true;
        }

        var from = args[1];
        var to = args[2];
        var force = false;

        if (args.length >= 4) {
            if (args[3].equals("true")) {
                force = true;
            }

            if (!args[3].equals("true") && !args[3].equals("false")) {
                LogUtil.send("参数 force? 只能为 true 或者 false。", sender);
                return true;
            }
        }

        var section = Files.selections.getConfigurationSection("data");

        if (section == null) {
            LogUtil.send("目前还没有任何检查点。", sender);
            return true;
        }

        var fromSection = section.getConfigurationSection(from);
        if (fromSection == null) {
            LogUtil.send(from + " 对应的路径点不存在。", sender);
            return true;
        }

        var toSection = section.getConfigurationSection(to);

        if (!force && toSection != null) {
            LogUtil.send(to + " 对应的路径点已经有值。如果需要覆盖，请在指令末尾加上 true。", sender);
            return true;
        }

        section.set(to, fromSection);
        Files.saveSelections();

        LogUtil.send("成功将 " + from + " 复制到 " + to + "。", sender);
        return true;
    }
}
