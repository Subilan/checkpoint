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
    public static File configFile;
    public static File tracksFile;
    public static File campaignFile;
    public static File analyticsFile;

    public static void init() {
        configFile = new File(
                datafolder.getAbsoluteFile() + "/config.yml"
        );
        tracksFile = new File(
                datafolder.getAbsoluteFile() + "/selections.yml"
        );
        campaignFile = new File(
                datafolder.getAbsoluteFile() + "/campaign.yml"
        );
        analyticsFile = new File(
                datafolder.getAbsoluteFile() + "/analytics.yml"
        );

        reload();
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        tracks = YamlConfiguration.loadConfiguration(tracksFile);
        campaigns = YamlConfiguration.loadConfiguration(campaignFile);
        analytics = YamlConfiguration.loadConfiguration(analyticsFile);
    }

    public static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSelections() {
        try {
            tracks.save(tracksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveCampaigns() {
        try {
            campaigns.save(campaignFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAnalytics() {
        try {
            analytics.save(analyticsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setDataFolder(File folder) {
        datafolder = folder;
    }
}
