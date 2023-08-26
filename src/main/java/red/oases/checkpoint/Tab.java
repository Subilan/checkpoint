package red.oases.checkpoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Objects.Progress;
import red.oases.checkpoint.Utils.FileUtils;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.PointUtils;
import red.oases.checkpoint.Utils.ProgressUtils;

import java.util.*;

public class Tab implements TabCompleter {

    public List<String> getAllPaths() {
        var result = new ArrayList<String>();
        var section = FileUtils.tracks.getConfigurationSection("data");

        if (section == null) {
            return List.of();
        }

        for (String key1 : CommonUtils.getTrackNames()) {
            var selectionKeys = Objects
                    .requireNonNull(section.getConfigurationSection(key1))
                    .getKeys(false);

            for (String key2 : selectionKeys) {
                result.add(key1 + "." + key2);
            }
        }

        return result;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!label.equals("checkpoint") && !label.equals("cpt")) return null;

        if (args.length == 0) return null;

        if (args.length == 1) {

            if (sender.hasPermission("checkpoint.admin")) return List.of(
                    "remove",
                    "build",
                    "list",
                    "about",
                    "copy",
                    "cp",
                    "move",
                    "xcopy",
                    "xcp",
                    "campaign",
                    "join",
                    "quit",
                    "reset",
                    "rank",
                    "mycampaign",
                    "forcecontinuous",
                    "setcheckpoint",
                    "teleport",
                    "tp",
                    "reload",
                    "switch",
                    "resume"
            );
            else return List.of(
                    "about",
                    "join",
                    "reset",
                    "rank",
                    "mycampaign",
                    "quit",
                    "teleport",
                    "tp",
                    "switch",
                    "resume"
            );
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "copy", "cp", "info" -> {
                    return getAllPaths();
                }

                case "remove", "list", "move", "forcecontinuous", "setcheckpoint" -> {
                    return new ArrayList<>(CommonUtils.getTrackNames());
                }

                case "xcp", "xcopy" -> {
                    return List.of("<T1.N1,T2.N2,...>");
                }

                case "rank", "reset", "switch" -> {
                    return new ArrayList<>(CommonUtils.getCampaignNames());
                }

                case "campaign" -> {
                    return List.of("setstatus", "new", "delete");
                }

                case "build" -> {
                    return List.of("<track>");
                }

                case "teleport", "tp" -> {
                    var p = (Player) sender;
                    var campaign = Progress.getRunningCampaign(p);
                    if (campaign == null) return List.of();
                    var available = PointUtils.getAvailableCheckpointsFor(p, campaign);
                    return available.stream().map(x -> x.number.toString()).toList();
                }
            }
        }

        if (args.length == 3) {
            switch (args[0]) {
                case "copy", "cp" -> {
                    return List.of("<track.number>");
                }

                case "remove", "setcheckpoint" -> {
                    var target = FileUtils.tracks.getConfigurationSection("data." + args[1]);
                    if (target == null) return List.of();
                    return new ArrayList<>(target.getKeys(false));
                }

                case "xcopy", "xcp" -> {
                    return List.of("<t1.n1,t2.n2,...>");
                }

                case "move" -> {
                    return List.of("<any-empty-track-name>");
                }

                case "campaign" -> {
                    switch (args[1]) {
                        case "setstatus", "delete" -> {
                            return new ArrayList<>(CommonUtils.getCampaignNames());
                        }

                        case "new" -> {
                            return List.of("<name>");
                        }
                    }
                }

                case "build" -> {
                    return List.of("<number>");
                }
            }
        }

        if (args.length == 4) {
            switch (args[0]) {
                case "copy", "cp", "xcopy", "xcp", "build", "setcheckpoint" -> {
                    return List.of("true", "false");
                }

                case "move" -> {
                    return List.of("<n1,n2,n3,...>");
                }

                case "campaign" -> {
                    switch (args[1]) {
                        case "setstatus" -> {
                            return List.of("open", "close", "private");
                        }

                        case "new" -> {
                            return new ArrayList<>(CommonUtils.getTrackNames());
                        }
                    }
                }
            }
        }

        return null;
    }
}
