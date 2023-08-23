package red.oases.checkpoint.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import red.oases.checkpoint.Utils.LogUtils;

public class Executor implements CommandExecutor {

    public static void sendAbout(CommandSender sender) {
        LogUtils.send("""
                                
                checkpoint v1.0
                设置路径点以监控玩家在拉力赛中的赛程数据
                适用于类似于喵窝 World Wings Rally 的比赛
                                
                https://github.com/oasis-mc/checkpoint""", sender);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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

                case "join" -> {
                    return new CommandJoin(args, sender).collect();
                }

                case "quit" -> {
                    return new CommandQuit(args, sender).collect();
                }

                case "reset" -> {
                    return new CommandReset(args, sender).collect();
                }

                case "rank" -> {
                    return new CommandRank(args, sender).collect();
                }

                case "campaign" -> {
                    return new CommandCampaign(args, sender).collect();
                }

                case "mycampaign" -> {
                    return new CommandMycampaign(args, sender).collect();
                }

                case "forcecontinuous" -> {
                    return new CommandForcecontinuous(args, sender).collect();
                }

                case "setcheckpoint" -> {
                    return new CommandSetcheckpoint(args, sender).collect();
                }

                case "tp", "teleport" -> {
                    return new CommandTeleport(args, sender).collect();
                }

                case "switch" -> {
                    return new CommandSwitch(args, sender).collect();
                }
            }
        }

        return false;
    }
}
