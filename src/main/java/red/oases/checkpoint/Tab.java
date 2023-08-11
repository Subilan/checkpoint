package red.oases.checkpoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Tab implements TabCompleter {

    public List<String> getAllPaths() {
        var result = new ArrayList<String>();
        var section = Files.selections.getConfigurationSection("data");

        if (section == null) {
            return List.of();
        }

        for (String key1 : Utils.getTrackNames()) {
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
                    "join"
            );
            else return List.of("join");
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "remove", "copy", "cp" -> {
                    return getAllPaths();
                }

                case "list", "move" -> {
                    return new ArrayList<>(Utils.getTrackNames());
                }

                case "xcp", "xcopy" -> {
                    return List.of("<T1.N1,T2.N2,...>");
                }

                case "join" -> {
                    return new ArrayList<>(Utils.getCampaignNames());
                }

                case "campaign" -> {
                    return List.of("setstaus", "new", "delete");
                }
            }
        }

        if (args.length == 3) {
            switch (args[0]) {
                case "copy", "cp" -> {
                    return List.of("<track.number>");
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
                            return new ArrayList<>(Utils.getCampaignNames());
                        }

                        case "new" -> {
                            return List.of("<name>");
                        }
                    }
                }
            }
        }

        if (args.length == 4) {
            switch (args[0]) {
                case "copy", "cp", "xcopy", "xcp" -> {
                    return List.of("true", "false");
                }

                case "move" -> {
                    return List.of("<n1,n2,n3,...>");
                }

                case "campaign" -> {
                    switch (args[1]) {
                        case "setstatus" -> {
                            return List.of("open", "close");
                        }
                    }
                }
            }
        }

        return null;
    }
}
