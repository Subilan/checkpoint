package red.oases.checkpoint.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import red.oases.checkpoint.LogUtil;

public class Executor implements CommandExecutor {

    public static void sendAbout(CommandSender sender) {
        LogUtil.send("""
                                
                checkpoint v1.0
                设置路径点以监控玩家在拉力赛中的赛程数据
                适用于类似于喵窝 World Wings Rally 的比赛
                                
                https://github.com/oasis-mc/checkpoint""", sender);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("checkpoint.admin")) {
            LogUtil.send("你需要拥有 checkpoint.admin 权限才可使用指令。", sender);
        }

        if (label.equalsIgnoreCase("checkpoint") || label.equalsIgnoreCase("cpt")) {
            if (args.length == 0) {
                sendAbout(sender);
                return true;
            }

            switch (args[0]) {
                case "copy", "cp" -> {
                    return new CommandCopy(args, sender).collect();
                }

                case "xcopy", "xcp" -> {
                    return new CommandXCopy(args, sender).collect();
                }

                case "move" -> {
                    return new CommandMove(args, sender).collect();
                }

                case "about" -> {
                    sendAbout(sender);
                    return true;
                }

                case "list" -> {
                    return new CommandList(args, sender).collect();
                }

                case "info" -> {
                    return new CommandInfo(args, sender).collect();
                }

                case "remove" -> {
                    return new CommandRemove(args,sender).collect();
                }

                case "build" -> {
                    return new CommandBuild(args, sender).collect();
                }
            }
        }

        return false;
    }
}
