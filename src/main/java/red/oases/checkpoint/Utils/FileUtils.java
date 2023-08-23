package red.oases.checkpoint.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static File datafolder;
    public static FileConfiguration config;
    public static FileConfiguration tracks;
    public static FileConfiguration campaigns;
    public static FileConfiguration analytics;
    public static FileConfiguration checkpoints;
    public static File fconfig;
    public static File ftracks;
    public static File fcampaigns;
    public static File fanalytics;
    public static File fcheckpoints;

    public static void init() {
        fconfig = new File(
                datafolder.getAbsoluteFile() + "/config.yml"
        );
        ftracks = new File(
                datafolder.getAbsoluteFile() + "/tracks.yml"
        );
        fcampaigns = new File(
                datafolder.getAbsoluteFile() + "/campaign.yml"
        );
        fanalytics = new File(
                datafolder.getAbsoluteFile() + "/analytics.yml"
        );
        fcheckpoints = new File(
                datafolder.getAbsoluteFile() + "/checkpoints.yml"
        );

        reload();
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(fconfig);
        tracks = YamlConfiguration.loadConfiguration(ftracks);
        campaigns = YamlConfiguration.loadConfiguration(fcampaigns);
        analytics = YamlConfiguration.loadConfiguration(fanalytics);
        checkpoints = YamlConfiguration.loadConfiguration(fcheckpoints);
    }

    public static void saveConfig() {
        try {
            config.save(fconfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSelections() {
        try {
            tracks.save(ftracks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveCampaigns() {
        try {
            campaigns.save(fcampaigns);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAnalytics() {
        try {
            analytics.save(fanalytics);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveProgress() {
        try {
            checkpoints.save(fcheckpoints);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setDataFolder(File folder) {
        datafolder = folder;
    }
}
