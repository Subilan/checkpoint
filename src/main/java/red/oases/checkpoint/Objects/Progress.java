package red.oases.checkpoint.Objects;

import org.bukkit.entity.Player;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class Progress {
    public static String checkpointPath(Player p, Campaign campaign) {
        return "checkpoint.%s.%s".formatted(campaign.getName(), p.getName());
    }

    public static void enableCheckpointFor(Player p, Point pt, Campaign campaign) {
        if (!pt.isCheckpoint()) return;
        var list = FileUtils.progress.getStringList(checkpointPath(p, campaign));
        list.add(identify(pt));
        FileUtils.progress.set(checkpointPath(p, campaign), list);
        FileUtils.saveProgress();
    }

//    public static void disableCheckpointFor(Player p, Point pt, Campaign campaign) {
//        if (!pt.isCheckpoint()) return;
//        var list = FileUtils.progress.getStringList(checkpointPath(p, campaign));
//        list.remove(identify(pt));
//        FileUtils.progress.set(checkpointPath(p, campaign), list);
//        FileUtils.saveProgress();
//    }

    public static void clearCheckpoints(Player p, Campaign campaign) {
        FileUtils.progress.set(checkpointPath(p, campaign), null);
        FileUtils.saveProgress();
    }

//    public static boolean isCheckpointFor(Player p, Point pt, Campaign campaign) {
//        if (!pt.isCheckpoint()) return false;
//        var list = FileUtils.progress.getStringList(checkpointPath(p, campaign));
//        return list.contains(identify(pt));
//    }

    public static List<Point> getAvailableCheckpointsFor(Player p, Campaign campaign) {
        var list = FileUtils.progress.getStringList(checkpointPath(p, campaign));
        return list.stream().map(s -> {
            var track = s.split("\\.")[0];
            var number = CommonUtils.mustPositive(s.split("\\.")[1]);
            return new Point(track, number);
        }).toList();
    }

    public static List<Point> getAllAvailableCheckpointsFor(Player p) {
        var campaigns = Campaign.get(p);
        var result = new ArrayList<Point>();
        for (var campaign : campaigns) {
            result.addAll(getAvailableCheckpointsFor(p, campaign));
        }
        return result;
    }

    public static String identify(Point pt) {
        return pt.track.name + "." + pt.number;
    }
}
