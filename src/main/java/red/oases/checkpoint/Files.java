package red.oases.checkpoint;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Files {
    public static File datafolder;
    public static FileConfiguration config;
    public static FileConfiguration selections;
    public static FileConfiguration campaigns;
    public static File configFile;
    public static File selectionsFile;
    public static File campaignFile;

    public static void init() {
        configFile = new File(
                datafolder.getAbsoluteFile() + "/config.yml"
        );
        selectionsFile = new File(
                datafolder.getAbsoluteFile() + "/selections.yml"
        );
        campaignFile = new File(
                datafolder.getAbsoluteFile() + "/campaign.yml"
        );

        reload();
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        selections = YamlConfiguration.loadConfiguration(selectionsFile);
        campaigns = YamlConfiguration.loadConfiguration(campaignFile);
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
            selections.save(selectionsFile);
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

    public static void setDataFolder(File folder) {
        datafolder = folder;
    }
}
