package red.oases.checkpoint.Objects;

import red.oases.checkpoint.Utils.FileUtils;

public class Config {

    public static String getString(String path) {
        return FileUtils.config.getString(path);
    }

    public static Boolean getBoolean(String path) {
        return FileUtils.config.getBoolean(path);
    }

    public static Integer getInteger(String path) {
        return FileUtils.config.getInt(path);
    }
}
