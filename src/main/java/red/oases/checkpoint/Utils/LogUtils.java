package red.oases.checkpoint.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

public final class LogUtils {
    public static Logger logger;

    public static void setLogger(Logger logger) {
        LogUtils.logger = logger;
    }

    public static void send(String text, CommandSender p) {
        p.sendMessage(Component
                .text("[")
                .append(Component
                        .text("cpt")
                        .color(NamedTextColor.YELLOW))
                .append(Component.text("] "))
                .append(Component.text(text)));
    }

    public static void sendWithoutPrefix(String text, CommandSender p) {
        p.sendMessage(Component
                .text(text));
    }
    
    @SuppressWarnings("deprecation")
    public static void sendLegacy(String text, CommandSender p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }
}