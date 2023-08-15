package red.oases.checkpoint.Objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Extra.Exceptions.ObjectNotFoundException;
import red.oases.checkpoint.Utils.FileUtils;

import java.util.Date;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Point {

    public Track track;
    public Integer number;

    public void delete() {
        this.track.getSection().set(this.number.toString(), null);
        FileUtils.saveSelections();
    }

    public void setCheckpoint(boolean yes) {
        getSection().set("is_checkpoint", yes);
        FileUtils.saveSelections();
    }

    public boolean isCheckpoint() {
        return getSection().getBoolean("is_checkpoint");
    }

    public static boolean isPresent(String trackname, Integer number) {
        return FileUtils.tracks.getConfigurationSection("data." + trackname + "." + number) != null;
    }

    public static void delete(String trackname, Integer number) {
        var track = new Track(trackname);
        track.getSection().set(number.toString(), null);
        FileUtils.saveSelections();
    }

    public static void build(String trackname, Integer number, List<Integer> pos1, List<Integer> pos2, String creator) {
        var section = FileUtils.tracks.createSection("data." + trackname + "." + number);
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
        if (this.track.getSection() == null) return null;
        return this.track.getSection().getConfigurationSection(this.number.toString());
    }

    public void setNumber(Integer number) {
        track.getSection().set(number.toString(), this.getSection());
        track.getSection().set(this.number.toString(), null);
        FileUtils.saveSelections();
    }

    public List<Integer> getFirstPosition() {
        return getSection().getIntegerList("pos1");
    }

    public List<Integer> getSecondPosition() {
        return getSection().getIntegerList("pos2");
    }

    public Date getCreatedAt() {
        return new Date(getSection().getLong("created_at"));
    }

    public String getCreator() {
        return getSection().getString("creator");
    }

    public boolean hasPrevious() {
        return track.getSection().getConfigurationSection(String.valueOf(this.number - 1)) != null;
    }

    public @Nullable Point getPrevious() {
        try {
            return new Point(this.track, this.number - 1);
        } catch (ObjectNotFoundException e) {
            return null;
        }
    }

    public boolean isLast() {
        return this.number == this.track.getSection().getKeys(false).size();
    }

    public boolean isFirst() {
        return this.number == 1;
    }

    private static IntSummaryStatistics getStats(List<Integer> input1, List<Integer> input2, int index) {
        return Stream.of(input1.get(index), input2.get(index))
                .collect(Collectors.summarizingInt(Integer::intValue));
    }

    public boolean covers(int x, int y, int z) {
        var pos1 = getFirstPosition();
        var pos2 = getSecondPosition();
        var X = getStats(pos1, pos2, 0);
        var Y = getStats(pos1, pos2, 1);
        var Z = getStats(pos1, pos2, 2);

        return (x >= X.getMin() && x <= X.getMax())
                && (y >= Y.getMin() && y <= Y.getMax())
                && (z >= Z.getMin() && z <= Z.getMax());
    }

    public Location getTransportableLocation(World world) {
        var pos1 = getFirstPosition();
        var pos2 = getSecondPosition();
        var X = getStats(pos1, pos2, 0);
        var Y = getStats(pos1, pos2, 1);
        var Z = getStats(pos1, pos2, 2);

        var x = (double) (X.getMax() + X.getMax()) / 2;
        var y = (double) Y.getMin() + 1;
        var z = (double) (Z.getMin() + Z.getMin()) / 2;

        return new Location(world, x, y, z);
    }
}
