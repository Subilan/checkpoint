package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
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
        var section = FileUtils.selections.getConfigurationSection("data." + track);

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

        var result = new StringBuilder("\n" + track + " 下的所有路径点\n\n");
        var keys = new ArrayList<>(section.getKeys(false));
        int iterationRangeStart;
        int iterationRangeEnd;
        var lastPage = (int) Math.ceil(keys.size() / 10d);

        if (page > lastPage) {
            if (page == 1) {
                LogUtils.send("此赛道下无数据。", sender);
            } else {
                LogUtils.send("页码过大。", sender);
            }
            return true;
        }

        if (keys.size() <= 10) {
            iterationRangeStart = 0;
            iterationRangeEnd = keys.size() - 1;
        } else {
            // 第一页index是0-9，第二页index是10-19以此类推
            iterationRangeStart = 10 * (page - 1);
            iterationRangeEnd = Math.min(iterationRangeStart + 9, keys.size() - 1);
        }

        for (var i = iterationRangeStart; i <= iterationRangeEnd; i++) {
            var k = keys.get(i);
            var targetSection = FileUtils.selections.getConfigurationSection(
                    String.format("data.%s.%s", track, k)
            );
            if (targetSection == null) continue;
            var pos1 = targetSection.getIntegerList("pos1");
            var pos2 = targetSection.getIntegerList("pos2");
            var creator = targetSection.getString("creator");
            result.append(String.format("[%s] (%s, %s, %s) - (%s, %s, %s) %s\n",
                    k,
                    pos1.get(0), pos1.get(1), pos1.get(2),
                    pos2.get(0), pos2.get(1), pos2.get(2),
                    creator
            ));
        }

        result.append(String.format(
                "\n第 %s 页 - 共 %s 页",
                page,
                lastPage
        ));

        LogUtils.send(result.toString(), sender);
        return true;
    }
}
