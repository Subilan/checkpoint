package red.oases.checkpoint.Objects;

import org.bukkit.configuration.ConfigurationSection;
import red.oases.checkpoint.Extra.Exceptions.ObjectNotFoundException;
import red.oases.checkpoint.Utils.FileUtils;

import java.util.Date;
import java.util.List;

public class Point {

    public Track track;
    public Integer number;

    public void delete() {
        this.track.getSection().set(this.number.toString(), null);
        FileUtils.saveSelections();
    }

    public static boolean isPresent(String trackname, Integer number) {
        var track = new Track(trackname);
        return track.getSection().getConfigurationSection(number.toString()) != null;
    }

    public static void delete(String trackname, Integer number) {
        var track = new Track(trackname);
        track.getSection().set(number.toString(), null);
        FileUtils.saveSelections();
    }

    public static void build(String trackname, Integer number, List<Integer> pos1, List<Integer> pos2, String creator) {
        var section = FileUtils.selections.createSection("data." + trackname + "." + number);
        section.set("pos1", pos1);
        section.set("pos2", pos2);
        section.set("creator", creator);
        section.set("created_at", new Date().getTime());
        FileUtils.saveSelections();
    }


    public Point(Track track, int number) {
        this.track = track;
        this.number = number;
        if (getSection() == null) throw new ObjectNotFoundException("point " + track + "." + number);
    }

    public Point(String trackname, int number) {
        this.track = new Track(trackname);
        this.number = number;
        if (getSection() == null) throw new ObjectNotFoundException("point " + track + "." + number);
    }

    public ConfigurationSection getSection() {
        return this.track.getSection().getConfigurationSection(this.number.toString());
    }

    public void setNumber(Integer number) {
        track.getSection().set(number.toString(), this.getSection());
        track.getSection().set(this.number.toString(), null);
        FileUtils.saveSelections();
    }

    public List<Integer> getFirstPosition() {
        return track.getSection().getIntegerList("pos1");
    }

    public List<Integer> getSecondPosition() {
        return track.getSection().getIntegerList("pos2");
    }

    public Date getCreatedAt() {
        return new Date(track.getSection().getLong("created_at"));
    }

    public String getCreator() {
        return track.getSection().getString("creator");
    }
}
