package red.oases.checkpoint;

import org.bukkit.plugin.java.JavaPlugin;

public final class CheckPoint extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("[checkpoint] 已启用");
        saveDefaultConfig();
        LogUtil.setLogger(this.getLogger());
        Files.setDataFolder(this.getDataFolder());
        Files.init();
        getServer().getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
        this.getLogger().info("[checkpoint] 已停用");
    }
}
