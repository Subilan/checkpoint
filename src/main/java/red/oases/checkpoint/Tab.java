package red.oases.checkpoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Tab implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!label.equals("checkpoint") && !label.equals("cpt")) return null;

        if (args.length == 0) return null;

        if (args.length == 1) {
            return List.of(
                    "remove",
                    "build"
            );
        }
        return null;
    }
}
