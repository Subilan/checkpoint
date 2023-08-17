package red.oases.checkpoint.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Objects.DisplayList;
import red.oases.checkpoint.Utils.FileUtils;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.CommonUtils;

import java.util.ArrayList;

public class CommandList extends Command {
    public CommandList(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足: /cpt list <track> [page]", sender);
            return true;
        }

        var track = args[1];
        var page = 1;
        var section = FileUtils.tracks.getConfigurationSection("data." + track);

        if (args.length == 3) {
            page = CommonUtils.mustPositive(args[2]);
            if (page == 0) {
                LogUtils.send("页码无效。", sender);
                return true;
            }
        }

        if (section == null) {
            LogUtils.send(String.format("找不到赛道 %s", track), sender);
            return true;
        }

        var keys = new ArrayList<>(section.getKeys(false));
        var list = new DisplayList(
                10,
                keys.size(),
                sender,
                DisplayList.getTitle(track, "下的所有路径点")
        );

        list.sendPage(page, i -> {
            var k = keys.get(i);
            var targetSection = FileUtils.tracks.getConfigurationSection(
                    String.format("data.%s.%s", track, k)
            );
            if (targetSection == null) return null;
            var pos1 = targetSection.getIntegerList("pos1");
            var pos2 = targetSection.getIntegerList("pos2");
            var creator = targetSection.getString("creator");
            return Component.empty()
                    .append(Component.text("(%s, %s, %s)".formatted(pos1.get(0), pos1.get(1), pos1.get(2))).color(NamedTextColor.YELLOW))
                    .appendSpace()
                    .append(Component.text("-").color(NamedTextColor.GRAY))
                    .appendSpace()
                    .append(Component.text("(%s, %s, %s)".formatted(pos2.get(0), pos2.get(1), pos2.get(2))).color(NamedTextColor.YELLOW))
                    .appendSpace()
                    .append(Component.text("由 %s 创建".formatted(creator)).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));
        });

        return true;
    }
}
