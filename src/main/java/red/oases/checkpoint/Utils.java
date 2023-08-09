package red.oases.checkpoint;

import java.util.HashSet;
import java.util.Set;

public class Utils {
    public static Set<String> getTracks() {
        var section = Files.selections.getConfigurationSection("data");

        if (section == null) {
            return new HashSet<>();
        } else {
            return section.getKeys(false);
        }
    }
}
