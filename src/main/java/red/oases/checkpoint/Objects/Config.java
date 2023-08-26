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

    public static boolean getAutoJoinOnLogin() {
        return getBoolean("auto-join-on-login");
    }

    public static boolean getAllowResume() {
        return getBoolean("allow-resume");
    }

    public static boolean getAutoResumeOnLogin() {
        return getBoolean("auto-resume-on-login");
    }

    public static boolean getDisableWarningAutoResume() {
        return getBoolean("disable-auto-resume-failure-warning");
    }

    public static Integer getTimerMaxTimeout() {
        return getInteger("timer-max-timeout");
    }

    public static boolean getDisallowTimerWorkingOffline() {
        return !getBoolean("allow-timer-working-offline");
    }

    public static boolean getDisableWarningAutoJoin() {
        return getBoolean("disable-auto-join-failure-warning");
    }

    public static Integer getHalfwayProgressDeadline() {
        return getInteger("halfway-progress-deadline");
    }
}
