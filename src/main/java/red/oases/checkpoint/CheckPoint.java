package red.oases.checkpoint;

import org.bukkit.plugin.java.JavaPlugin;
import red.oases.checkpoint.Commands.Executor;

import java.util.Objects;

@SuppressWarnings("unused")
public final class CheckPoint extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("[checkpoint] 已启用");
        saveDefaultConfig();
        LogUtil.setLogger(this.getLogger());
        Files.setDataFolder(this.getDataFolder());
        Files.init();
        getServer().getPluginManager().registerEvents(new Events(), this);
        Objects.requireNonNull(getCommand("checkpoint")).setExecutor(new Executor());
        Objects.requireNonNull(getCommand("checkpoint")).setTabCompleter(new Tab());
    }

    @Override
    public void onDisable() {
        this.getLogger().info("[checkpoint] 已停用");
    }
}
