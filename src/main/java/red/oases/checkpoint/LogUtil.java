package red.oases.checkpoint;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public final class LogUtil {
    public static Logger logger;

    public static void setLogger(Logger logger) {
        LogUtil.logger = logger;
    }

    public static void send(String text, Player p) {
        p.sendMessage(Component
                .text("[")
                .append(Component
                        .text("checkpoint")
                        .color(NamedTextColor.YELLOW))
                .append(Component.text("] "))
                .append(Component.text(text)));
    }
}