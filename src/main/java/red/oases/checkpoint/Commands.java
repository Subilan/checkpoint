package red.oases.checkpoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Commands implements CommandExecutor {

    public List<String> getTracks() {
        var section = Files.selections.getConfigurationSection("data");

        if (section == null) {
            return List.of();
        } else {
            return new ArrayList<>(section.getKeys(false));
        }
    }

    public @Nullable String composeGetPath(String target, boolean isAlias) {
        if (isAlias) {
            return getPathByAlias(target);
        } else {
            return String.format("data.%s", target);
        }
    }

    /**
     * 获得指向指定 path 的别名
     *
     * @param path 指定 path，不带 data. 前缀
     * @return 指定别名。如果不存在，返回 ""
     */
    public String getAliasByPath(String path) {
        var result = new AtomicReference<>("");

        var aliasSection = Files.selections.getConfigurationSection("aliases");

        if (aliasSection != null) {
            var map = aliasSection.getValues(false);
            map.forEach((key, value) -> {
                if (value.equals("data." + path)) result.set(key);
            });
        }

        return result.get();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("checkpoint.admin")) {
            LogUtil.send("你需要拥有 checkpoint.admin 权限才可使用指令。", sender);
        }

        if (label.equalsIgnoreCase("checkpoint") || label.equalsIgnoreCase("cpt")) {
            if (args.length == 0) {
                sendAbout(sender);
                return true;
            }

            switch (args[0]) {
                case "copy", "cp" -> {
                    if (args.length < 3) {
                        LogUtil.send("参数不足：/cpt copy <from-track.number> <to-track.number> [force?]", sender);
                        return true;
                    }

                    var from = args[1];
                    var to = args[2];
                    var force = false;

                    if (args.length >= 4) {
                        if (args[3].equals("true")) {
                            force = true;
                        }

                        if (!args[3].equals("true") && !args[3].equals("false")) {
                            LogUtil.send("参数 force? 只能为 true 或者 false。", sender);
                            return true;
                        }
                    }

                    var section = Files.selections.getConfigurationSection("data");

                    if (section == null) {
                        LogUtil.send("目前还没有任何检查点。", sender);
                        return true;
                    }

                    var fromList = section.getStringList(from);
                    if (fromList.isEmpty()) {
                        LogUtil.send(from + " 对应的路径点不存在。", sender);
                        return true;
                    }

                    var toList = section.getStringList(to);

                    if (!force && !toList.isEmpty()) {
                        LogUtil.send(to + " 对应的路径点已经有值。如果需要覆盖，请在指令末尾加上 true。", sender);
                        return true;
                    }

                    section.set(to, fromList);
                    Files.saveSelections();

                    LogUtil.send("成功将 " + from + " 复制到 " + to + "。", sender);
                    return true;
                }

                case "move" -> {
                    if (args.length < 4) {
                        LogUtil.send("参数不足：/cpt move <from-track> <to-track> <numbers>", sender);
                        return true;
                    }

                    var tracks = getTracks();
                    var fromTrack = args[1];
                    var toTrack = args[2];
                    var numbers = args[3].split(",");

                    if (!tracks.contains(fromTrack)) {
                        LogUtil.send("赛道不存在：" + fromTrack + "。", sender);
                        return true;
                    }

                    if (tracks.contains(toTrack)) {
                        LogUtil.send("赛道 " + toTrack + " 不为空。", sender);
                        return true;
                    }

                    var targetNumbers = new HashSet<Integer>();

                    for (var n : numbers) {
                        if (n.contains("-")) {
                            var numberRange = n.split("-");

                            if (numberRange.length != 2) {
                                LogUtil.send("数字范围不合法：" + n, sender);
                                return true;
                            }

                            var numberRangeStart = positive(numberRange[0]);
                            var numberRangeEnd = positive(numberRange[1]);

                            if (numberRangeStart == 0 || numberRangeEnd == 0) {
                                LogUtil.send("数字范围不合法：" + n, sender);
                                return true;
                            }

                            for (var nn = numberRangeStart; nn <= numberRangeEnd; nn++) {
                                targetNumbers.add(nn);
                            }
                        } else {
                            var nn = positive(n);
                            if (nn == 0) {
                                LogUtil.send("序号不合法：" + n, sender);
                                return true;
                            }
                            targetNumbers.add(nn);
                        }
                    }

                    var section = Files.selections.getConfigurationSection("data");
                    assert section != null;
                    assert section.getConfigurationSection(fromTrack) != null;
                    assert section.getConfigurationSection(toTrack) == null;
                    var index = 1;

                    for (var number : targetNumbers) {
                        var tg = section.getStringList(fromTrack + "." + number);
                        section.set(toTrack + "." + index, tg);
                        section.set(fromTrack + "." + number, null);
                        index++;
                    }

                    Files.saveSelections();

                    LogUtil.send("成功移动 " + index + " 个检查点。", sender);
                    return true;
                }

                case "about" -> {
                    sendAbout(sender);
                    return true;
                }

                case "list" -> {
                    if (args.length < 2) {
                        LogUtil.send("参数不足: /cpt list <track> [page]", sender);
                        return true;
                    }

                    var track = args[1];
                    var page = 1;
                    var section = Files.selections.getConfigurationSection("data." + track);

                    if (args.length == 3) {
                        page = positive(args[2]);
                        if (page == 0) {
                            LogUtil.send("页码无效。", sender);
                            return true;
                        }
                    }

                    if (section == null) {
                        LogUtil.send(String.format("找不到命名空间 %s", track), sender);
                        return true;
                    }

                    var result = new StringBuilder("\n" + track + " 下的所有路径点\n\n");
                    var keys = new ArrayList<>(section.getKeys(false));
                    int iterationRangeStart;
                    int iterationRangeEnd;
                    var lastPage = (int) Math.ceil(keys.size() / 10d);

                    if (page > lastPage) {
                        LogUtil.send("页码过大。", sender);
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
                        var targetSection = Files.selections.getConfigurationSection(
                                String.format("data.%s.%s", track, k)
                        );
                        if (targetSection == null) continue;
                        var pos1 = targetSection.getStringList("pos1");
                        var pos2 = targetSection.getStringList("pos2");
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

                    LogUtil.send(result.toString(), sender);
                    return true;
                }

                case "info" -> {
                    if (args.length == 1) {
                        LogUtil.send("参数不足: /cpt info <alias> 或者 /cpt info <track.number>", sender);
                        return true;
                    }

                    var target = args[1];
                    var isAlias = !target.contains(".");
                    var path = composeGetPath(target, isAlias);

                    if (path == null) {
                        if (isAlias) {
                            LogUtil.send(String.format("别名 %s 不存在。", target), sender);
                        } else {
                            LogUtil.send(String.format("路径点 %s 不存在。", target), sender);
                        }
                        return true;
                    }

                    var pos1 = Files.selections.getStringList(path + ".pos1");
                    var pos2 = Files.selections.getStringList(path + ".pos2");
                    var creator = Files.selections.getString(path + ".creator");
                    var createdAt = Files.selections.getLong(path + ".created_at");

                    var result = String.format(
                            """
                                                                        
                                    ---路径点 %s 的详细信息---
                                    顶点 1: (%s, %s, %s)
                                    顶点 2: (%s, %s, %s)
                                    由 %s 创建于 %s""",
                            path,
                            pos1.get(0), pos1.get(1), pos1.get(2),
                            pos2.get(0), pos2.get(1), pos2.get(2),
                            creator, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(createdAt))
                    );

                    var targetAlias = isAlias ? target : getAliasByPath(target);

                    if (!targetAlias.equalsIgnoreCase("")) {
                        result += String.format("\n别名: %s", targetAlias);
                    }

                    LogUtil.send(result, sender);
                    return true;
                }

                case "remove" -> {
                    if (args.length == 1) {
                        LogUtil.send("参数不足: /cpt remove <alias> 或者 /cpt remove <track.number>", sender);
                        return true;
                    }

                    var target = args[1];
                    var isAlias = !target.contains(".");
                    var path = composeGetPath(target, isAlias);

                    if (path == null) {
                        if (isAlias) {
                            LogUtil.send(String.format("别名 %s 不存在。", target), sender);
                        } else {
                            LogUtil.send(String.format("路径点 %s 不存在。", target), sender);
                        }
                        return true;
                    }

                    Files.selections.set(path, null);
                    Files.saveSelections();

                    LogUtil.send(isAlias
                            ? String.format("成功删除别名 %s 对应的路径点 %s", target, path)
                            : String.format("成功删除路径点 %s", path), sender);

                    return true;
                }

                case "build" -> {
                    if (!(sender instanceof Player p)) {
                        LogUtil.send("此指令只能由玩家执行。", sender);
                        return true;
                    }

                    if (args.length < 3) {
                        LogUtil.send("参数不足: /cpt build <track> <number> [alias]", sender);
                        return true;
                    }

                    if (args[2].contains(".")) {
                        LogUtil.send("错误: 序号必须为整数。", sender);
                        return true;
                    }

                    var track = args[1];
                    var number = positive(args[2]);
                    String alias = "";

                    if (number <= 0) {
                        LogUtil.send("错误: 序号必须为非负整数。", sender);
                        return true;
                    }

                    if (args.length == 4) {
                        alias = args[3];
                    }

                    var playerId = p.getUniqueId().toString();

                    if (Selection.getState(playerId) != 2) {
                        LogUtil.send("错误: 必须在已选择两对角线顶点的情况下执行该指令。", sender);
                        return true;
                    }

                    var pos1 = Selection.getData(playerId, 1);
                    var pos2 = Selection.getData(playerId, 2);
                    var path = String.format("data.%s.%s", track, number);
                    Files.selections.set(path + ".pos1", pos1);
                    Files.selections.set(path + ".pos2", pos2);
                    Files.selections.set(path + ".creator", p.getName());
                    Files.selections.set(path + ".created_at", new Date().getTime());
                    if (!alias.isEmpty()) {
                        Files.selections.set("aliases." + alias, path);
                    }
                    Files.saveSelections();
                    Selection.clear(playerId);

                    LogUtil.send("成功: 已创建路径点。", sender);
                    return true;
                }
            }
        }
        return false;
    }

    public void sendAbout(CommandSender sender) {
        LogUtil.send("""
                                
                checkpoint v1.0
                设置路径点以监控玩家在拉力赛中的赛程数据
                适用于类似于喵窝 World Wings Rally 的比赛
                                
                https://github.com/oasis-mc/checkpoint""", sender);
    }

    /**
     * 转换对应字符串为非负整数。如果转换失败，返回 0。
     *
     * @param target 待转换的字符串
     * @return 成功为对应整数，不成功为 0
     */
    public int positive(String target) {
        int result;
        try {
            result = Integer.parseInt(target);
        } catch (NumberFormatException e) {
            return 0;
        }
        return result;
    }

    public String getPathByAlias(String alias) {
        return Files.selections.getString(String.format("aliases.%s", alias));
    }
}
