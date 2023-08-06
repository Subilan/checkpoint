package red.oases.checkpoint;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Files {
    public static File datafolder;
    public static FileConfiguration config;
    public static FileConfiguration selections;
    public static File configFile;
    public static File selectionsFile;

    public static void init() {
        configFile = new File(
                datafolder.getAbsoluteFile() + "/config.yml"
        );
        selectionsFile = new File(
                datafolder.getAbsoluteFile() + "/selections.yml"
        );
        reload();
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        selections = YamlConfiguration.loadConfiguration(selectionsFile);
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

    public static void setDataFolder(File folder) {
        datafolder = folder;
    }
}
