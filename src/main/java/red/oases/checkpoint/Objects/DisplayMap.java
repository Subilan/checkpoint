package red.oases.checkpoint.Objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class DisplayMap {
    public CommandSender sender;
    public Component title;
    public Map<String, Component> map;
    public static final Component divider = Component.text("=-=-=-=-=-=").color(NamedTextColor.GRAY);

    public DisplayMap(Component title, CommandSender sender, Map<String, Component> map) {
        this.sender = sender;
        this.title = title;
        this.map = map;
    }

    public void send() {
        var result = Component
                .newline().append(divider)
                .appendNewline().append(title)
                .appendNewline().append(divider)
                .appendNewline()
                .appendNewline();

        for (var k : map.keySet()) {
            result = result
                    .append(Component.text(k).color(NamedTextColor.GREEN))
                    .appendSpace()
                    .append(Component.text("-").color(NamedTextColor.GRAY))
                    .appendSpace()
                    .append(map.get(k))
                    .appendNewline();
        }

        result = result.appendNewline();

        sender.sendMessage(result);
    }
}
