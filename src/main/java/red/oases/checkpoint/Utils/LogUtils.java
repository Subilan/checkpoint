package red.oases.checkpoint.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

public final class LogUtils {
    public static Logger logger;

    public static void setLogger(Logger logger) {
        LogUtils.logger = logger;
    }

    public static Component getDefaultPrefix() {
        return Component
                .text("[")
                .append(t("cpt", NamedTextColor.YELLOW))
                .append(t("] "));
    }

    public static Component t(String str) {
        return Component.text(str);
    }

    public static Component t(String str, TextColor color) {
        return Component.text(str).color(color);
    }

    public static Component getPrefixedDefaultPrefix(Component prefix) {
        return getDefaultPrefix()
                .append(prefix);
    }

    public static void send(String text, CommandSender p) {
        p.sendMessage(getDefaultPrefix().append(t(text)));
    }

    public static void sendError(String text, CommandSender p) {
        p.sendMessage(getPrefixedDefaultPrefix(t("错误", NamedTextColor.RED).append(t(text))));
    }

    public static void sendSuccess(String text, CommandSender p) {
        p.sendMessage(getPrefixedDefaultPrefix(t("成功", NamedTextColor.GREEN).append(t(text))));
    }

    public static void sendWithoutPrefix(String text, CommandSender p) {
        p.sendMessage(t(text));
    }

    @SuppressWarnings("deprecation")
    public static void sendLegacy(String text, CommandSender p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }
}