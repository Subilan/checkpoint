package red.oases.checkpoint.Utils;

import org.bukkit.entity.Player;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.Point;

import java.util.List;

public class PointUtils {
    public static void clearCheckpoints(Player p, Campaign campaign) {
        FileUtils.progress.set(checkpointPath(p, campaign), null);
        FileUtils.saveProgress();
    }

    public static String checkpointPath(Player p, Campaign campaign) {
        return "checkpoint.%s.%s".formatted(campaign.getName(), p.getName());
    }

    public static List<Point> getAllCheckpoints(Campaign campaign) {
        return campaign.getTrack().getPoints().stream().filter(Point::isCheckpoint).toList();
    }

    public static void enableCheckpointFor(Player p, Point pt, Campaign campaign) {
        if (!pt.isCheckpoint()) return;
        var list = FileUtils.progress.getStringList(checkpointPath(p, campaign));
        list.add(identify(pt));
        FileUtils.progress.set(checkpointPath(p, campaign), list);
        FileUtils.saveProgress();
    }

    public static List<Point> getAvailableCheckpointsFor(Player p, Campaign campaign) {
        var list = FileUtils.progress.getStringList(checkpointPath(p, campaign));
        return list.stream().map(s -> {
            var track = s.split("\\.")[0];
            var number = CommonUtils.mustPositive(s.split("\\.")[1]);
            return new Point(track, number);
        }).toList();
    }

    public static String identify(Point pt) {
        return pt.track.name + "." + pt.number;
    }


    //    public static void disableCheckpointFor(Player p, Point pt, Campaign campaign) {
//        if (!pt.isCheckpoint()) return;
//        var list = FileUtils.progress.getStringList(checkpointPath(p, campaign));
//        list.remove(identify(pt));
//        FileUtils.progress.set(checkpointPath(p, campaign), list);
//        FileUtils.saveProgress();
//    }

    //    public static boolean isCheckpointFor(Player p, Point pt, Campaign campaign) {
//        if (!pt.isCheckpoint()) return false;
//        var list = FileUtils.progress.getStringList(checkpointPath(p, campaign));
//        return list.contains(identify(pt));
//    }
}
