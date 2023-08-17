package red.oases.checkpoint.Objects;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Extra.Interfaces.ListItemCallback;
import red.oases.checkpoint.Utils.LogUtils;

import java.util.Objects;

public class DisplayList {
    public int pagin;
    public int totalItemCount;
    public CommandSender sender;
    public String title;

    public DisplayList(int maxItemPerPage, int totalItemCount, CommandSender sender, String title) {
        this.pagin = maxItemPerPage;
        this.totalItemCount = totalItemCount;
        this.sender = sender;
        this.title = title;
    }

    public void sendPage(int page, ListItemCallback cb) {
        var result = new StringBuilder(this.title + "\n\n");

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
            var res = cb.run(i);
            if (Objects.equals(res, "continue")) continue;
            result.append(res);
        }

        result.append(String.format(
                "\n第 %s 页 - 共 %s 页",
                page,
                lastPage
        ));

        LogUtils.sendWithoutPrefix(result.toString(), sender);
    }

}
