package red.oases.checkpoint.Objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Extra.Interfaces.ListItemCallbackComponent;
import red.oases.checkpoint.Utils.LogUtils;

public class DisplayList {
    public int pagin;
    public int totalItemCount;
    public CommandSender sender;
    public Component title;
    public static final Component divider = Component.text("=-=-=-=-=-=").color(NamedTextColor.GRAY);

    public static Component getTitle(String a, String b) {
        return Component.text(a).color(NamedTextColor.YELLOW)
                .appendSpace()
                .append(Component.text(b).color(NamedTextColor.GREEN));
    }

    public DisplayList(int maxItemPerPage, int totalItemCount, CommandSender sender, Component title) {
        this.pagin = maxItemPerPage;
        this.totalItemCount = totalItemCount;
        this.sender = sender;
        this.title = title;
    }

    public void sendPage(int page, ListItemCallbackComponent cb) {
        var result = Component
                .newline().append(divider)
                .appendNewline().append(title)
                .appendNewline().append(divider)
                .appendNewline()
                .appendNewline();

        int iterationRangeStart;
        int iterationRangeEnd;
        var lastPage = (int) Math.ceil(totalItemCount / 10d);

        if (page > lastPage) {
            if (page == 1) {
                LogUtils.send("暂无数据。", sender);
            } else {
                LogUtils.send("页码过大。", sender);
            }
            return;
        }

        if (totalItemCount <= 10) {
            iterationRangeStart = 0;
            iterationRangeEnd = totalItemCount - 1;
        } else {
            // 第一页index是0-9，第二页index是10-19以此类推
            iterationRangeStart = 10 * (page - 1);
            iterationRangeEnd = Math.min(iterationRangeStart + 9, totalItemCount - 1);
        }

        for (var i = iterationRangeStart; i <= iterationRangeEnd; i++) {
            var res = cb.getComponent(i);
            if (res == null) continue;
            result = result.append(res);
        }

        result = result.appendNewline()
                .append(Component.empty()
                        .append(Component.text("· ").color(NamedTextColor.GRAY))
                        .append(Component.text("第 "))
                        .append(Component.text(page).color(NamedTextColor.YELLOW)))
                        .append(Component.text("/%s".formatted(lastPage)).color(NamedTextColor.GRAY))
                        .append(Component.text(" 页"))
                        .append(Component.text(" ·").color(NamedTextColor.GRAY))
                        .color(NamedTextColor.WHITE);

        LogUtils.sendWithoutPrefix(result, sender);
    }
}
