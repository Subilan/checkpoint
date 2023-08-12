package red.oases.checkpoint;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class Track {
    public String name;
    public ConfigurationSection section;

    public Track(String t) {
        this.name = t;
    }

    public boolean isPresent() {
        return Files.selections.getConfigurationSection("data." + this.name) != null;
    }

    public ConfigurationSection getSection() {
        return Files.selections.getConfigurationSection("data." + this.name);
    }

    public List<Point> getPoints() {
        var result = new ArrayList<Point>();
        for (var k : this.getSection().getKeys(false)) {
            var i = Utils.mustPositive(k);
            if (i == 0) continue;
            result.add(new Point(this.name, i));
        }
        return result;
    }
}
