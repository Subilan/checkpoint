package red.oases.checkpoint.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Objects.DisplayList;
import red.oases.checkpoint.Objects.Track;
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

        var tr = args[1];
        var page = 1;
        var section = FileUtils.tracks.getConfigurationSection("data." + tr);

        if (args.length == 3) {
            page = CommonUtils.mustPositive(args[2]);
            if (page == 0) {
                LogUtils.send("页码无效。", sender);
                return true;
            }
        }

        if (section == null) {
            LogUtils.send(String.format("找不到赛道 %s", tr), sender);
            return true;
        }

        var keys = new ArrayList<>(section.getKeys(false));
        var list = new DisplayList(
                10,
                keys.size(),
                sender,
                DisplayList.getTitle(tr, "下的所有路径点")
        );
        var track = new Track(tr);

        list.sendPage(page, i -> {
            var pt = track.getPoints().get(i);
            var pos1 = pt.getFirstPosition();
            var pos2 = pt.getSecondPosition();
            var creator = pt.getCreator();
            var date = CommonUtils.formatDate(pt.getCreatedAt());
            return Component.empty()
                    .append(Component.text("[").color(NamedTextColor.GRAY))
                    .append(Component.text(i + 1).color(NamedTextColor.GREEN))
                    .append(Component.text("]").color(NamedTextColor.GRAY))
                    .appendSpace()
                    .append(Component.text("(%s, %s, %s)".formatted(pos1.get(0), pos1.get(1), pos1.get(2))).color(NamedTextColor.YELLOW))
                    .appendSpace()
                    .append(Component.text("-").color(NamedTextColor.GRAY))
                    .appendSpace()
                    .append(Component.text("(%s, %s, %s)".formatted(pos2.get(0), pos2.get(1), pos2.get(2))).color(NamedTextColor.YELLOW))
                    .appendSpace()
                    .append(Component.text("由 %s 创建于 %s".formatted(creator, date)).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    .appendNewline();
        });

        return true;
    }
}
