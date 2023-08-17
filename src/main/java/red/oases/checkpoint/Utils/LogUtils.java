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

    public static Component getListItem(Integer index, String str) {
        return t("[")
                .append(t(index.toString(), NamedTextColor.GREEN))
                .append(t("] "))
                .append(t(str));
    }

    public static TextColor getTextColorByIndex(int index) {
        switch (index) {
            case 1 -> {
                return NamedTextColor.AQUA;
            }
            case 2 -> {
                return NamedTextColor.GREEN;
            }
            case 3 -> {
                return NamedTextColor.YELLOW;
            }
            default -> {
                return NamedTextColor.GOLD;
            }
        }
    }

    public static Component getListItemColored(Integer index, String str) {
        return getListItemColored(index, t(str));
    }

    public static Component getListItemColored(Integer index, Component str) {
        return t("[")
                .append(t(index.toString(), getTextColorByIndex(index)))
                .append(t("] "))
                .append(str);
    }

    public static Component getPrefixedDefaultPrefix(Component prefix) {
        return getDefaultPrefix()
                .append(prefix);
    }

    public static void send(String text, CommandSender p) {
        p.sendMessage(getDefaultPrefix().append(t(text)));
    }

    public static void sendError(String text, CommandSender p) {
        p.sendMessage(getPrefixedDefaultPrefix(t("错误 ", NamedTextColor.RED).append(t(text))));
    }

    public static void sendSuccess(String text, CommandSender p) {
        p.sendMessage(getPrefixedDefaultPrefix(t("成功 ", NamedTextColor.GREEN).append(t(text))));
    }

    public static void sendWithoutPrefix(String text, CommandSender p) {
        sendWithoutPrefix(t(text), p);
    }

    public static void sendWithoutPrefix(Component comp, CommandSender p) {
        p.sendMessage(comp);
    }

    @SuppressWarnings("deprecation")
    public static void sendLegacy(String text, CommandSender p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }
}