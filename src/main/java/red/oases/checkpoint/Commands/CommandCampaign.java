package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Commands.SubCommands.CommandCampaignDelete;
import red.oases.checkpoint.Commands.SubCommands.CommandCampaignNew;
import red.oases.checkpoint.Commands.SubCommands.CommandCampaignSetstatus;
import red.oases.checkpoint.Utils.LogUtils;

public class CommandCampaign extends Command {
    public CommandCampaign(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足：/cpt campaign <action> [args...]", sender);
            return true;
        }

        var action = args[1];

        switch (action) {
            case "new" -> {
                return new CommandCampaignNew(args, sender).collect();
            }
            case "delete" -> {
                return new CommandCampaignDelete(args, sender).collect();
            }
            case "setstatus" -> {
                return new CommandCampaignSetstatus(args, sender).collect();
            }
        }

        return true;
    }
}
