package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Commands.SubCommands.CommandCampaignDelete;
import red.oases.checkpoint.Commands.SubCommands.CommandCampaignNew;
import red.oases.checkpoint.LogUtil;

public class CommandCampaign extends Command {
    public CommandCampaign(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 2) {
            LogUtil.send("参数不足：/cpt campaign <action> [args...]", sender);
            return true;
        }

        var action = args[1];

        switch (action) {
            case "new" -> new CommandCampaignNew(args, sender).collect();
            case "delete" -> new CommandCampaignDelete(args, sender).collect();
        }

        return true;
    }
}
