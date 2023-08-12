package red.oases.checkpoint.Objects;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.TimerTask;

public class DedicatedPlayerTimer {
    public Player player;
    public boolean isLocked;

    public DedicatedPlayerTimer(Player p) {
        this.player = p;
    }

    public void startTimerFor(Player p, @NotNull Point pt) {
        if (this.isLocked) return;
        PlayerTimer.renewTimer(p);
        PlayerTimer.timerStorage.set(pt.number.toString(), 0);
        PlayerTimer.getTimer(p).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                PlayerTimer.tick(p, pt.number);
            }
        }, 0, 1000);
        this.isLocked = true;
    }

    public void stopTimerFor(Player p, @NotNull Point pt) {
        PlayerTimer.getTimer(p).cancel();
        this.isLocked = false;
    }
}
