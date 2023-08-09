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

        for (String key1 : Utils.getTracks()) {
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
            return List.of(
                    "remove",
                    "build",
                    "list",
                    "about",
                    "copy",
                    "cp",
                    "move"
            );
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "remove", "copy", "cp" -> {
                    return getAllPaths();
                }

                case "list", "move" -> {
                    return new ArrayList<>(Utils.getTracks());
                }
            }
        }

        if (args.length == 3) {
            switch (args[0]) {
                case "copy", "cp" -> {
                    return List.of("<track.number>");
                }

                case "move" -> {
                    return List.of("<any-empty-track-name>");
                }
            }
        }

        if (args.length == 4) {
            switch (args[0]) {
                case "copy", "cp" -> {
                    return List.of("true", "false");
                }

                case "move" -> {
                    return List.of("<n1,n2,n3,...>");
                }
            }
        }

        return null;
    }
}
