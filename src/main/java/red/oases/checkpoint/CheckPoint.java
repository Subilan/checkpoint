package red.oases.checkpoint;

import org.bukkit.plugin.java.JavaPlugin;

public final class CheckPoint extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        LogUtil.setLogger(this.getLogger());
        Files.setDataFolder(this.getDataFolder());
        Files.init();
        getServer().getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
