package red.oases.checkpoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class Commands implements CommandExecutor {

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
                case "info" -> {
                    if (args.length == 1) {
                        LogUtil.send("参数不足: /cpt info <alias> 或者 /cpt info <namespace.number>", sender);
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
                        LogUtil.send("参数不足: /cpt remove <alias> 或者 /cpt remove <namespace.number>", sender);
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
                        LogUtil.send("参数不足: /cpt build <namespace> <number> [alias]", sender);
                        return true;
                    }

                    if (args[2].contains(".")) {
                        LogUtil.send("错误: 序号必须为整数。", sender);
                        return true;
                    }

                    var namespace = args[1];
                    var number = castNonNegative(args[2]);
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
                    var path = String.format("data.%s.%s", namespace, number);
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
    public int castNonNegative(String target) {
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
