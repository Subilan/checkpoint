package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;

public class Command {
    public String[] args;
    public CommandSender sender;

    public Command(String[] args, CommandSender sender) {
        this.args = args;
        this.sender = sender;
    }
}
