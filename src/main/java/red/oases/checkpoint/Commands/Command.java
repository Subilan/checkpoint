package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Annotations.DisableConsole;
import red.oases.checkpoint.Annotations.PermissionLevel;
import red.oases.checkpoint.LogUtil;

public abstract class Command {
    public String[] args;
    public CommandSender sender;
    public int permissionLevel = 1;
    public boolean disableConsole;

    public Command(String[] args, CommandSender sender) {
        this.args = args;
        this.sender = sender;
        if (this.getClass().isAnnotationPresent(PermissionLevel.class)) {
            this.permissionLevel = this.getClass().getAnnotation(PermissionLevel.class).value();
        }
        this.disableConsole = this.getClass().isAnnotationPresent(DisableConsole.class);
    }

    protected abstract boolean execute();

    public boolean collect() {

        if (permissionLevel > 0 && !sender.hasPermission("checkpoint.admin")) {
            LogUtil.send("你没有足够的权限执行此指令。", sender);
            return true;
        }

        if (disableConsole && !(sender instanceof Player)) {
            LogUtil.send("此指令只能由玩家执行。", sender);
            return true;
        }

        return this.execute();
    }
}