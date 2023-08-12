package red.oases.checkpoint.Objects;

import org.bukkit.configuration.ConfigurationSection;
import red.oases.checkpoint.Utils.FileUtils;
import red.oases.checkpoint.Utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class Track {
    public String name;
    public ConfigurationSection section;

    public Track(String t) {
        this.name = t;
    }

    public boolean isPresent() {
        return FileUtils.selections.getConfigurationSection("data." + this.name) != null;
    }

    public ConfigurationSection getSection() {
        return FileUtils.selections.getConfigurationSection("data." + this.name);
    }

    public List<Point> getPoints() {
        var result = new ArrayList<Point>();
        var keys = this.getSection().getKeys(false)
                .stream()
                .sorted()
                .toList();
        for (var k : keys) {
            var i = CommonUtils.mustPositive(k);
            if (i == 0) continue;
            result.add(new Point(this.name, i));
        }
        return result;
    }
}
