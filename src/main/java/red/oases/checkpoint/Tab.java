package red.oases.checkpoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tab implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!label.equals("checkpoint") && !label.equals("cpt")) return null;

        if (args.length == 0) return null;

        if (args.length == 1) {
            return List.of(
                    "remove",
                    "build",
                    "list",
                    "about"
            );
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "remove" -> {
                    var result = new ArrayList<String>();

                    var section = Files.selections.getConfigurationSection("data");

                    if (section == null) {
                        return null;
                    }

                    var namespaceKeys = section.getKeys(false);

                    for (String key1 : namespaceKeys) {
                        var selectionKeys = Objects
                                .requireNonNull(section.getConfigurationSection(key1))
                                .getKeys(false);

                        for (String key2 : selectionKeys) {
                            result.add(key1 + "." + key2);
                        }
                    }

                    return result;
                }

                case "list" -> {
                    var namespaces = Files.selections.getConfigurationSection("data");
                    if (namespaces == null) {
                        return null;
                    }
                    return new ArrayList<>(namespaces.getKeys(false));
                }
            }
        }

        return null;
    }
}
