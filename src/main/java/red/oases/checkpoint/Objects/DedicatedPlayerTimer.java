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

    public void startTimerFor(Player p, @NotNull Campaign campaign, @NotNull Point pt, Long initialValue) {
        if (this.isLocked) return;
        PlayerTimer.renewTimer(p);
        PlayerTimer.timerStorage.set(
                Path.timer(p, campaign, pt.number),
                initialValue
        );
        PlayerTimer.getTimer(p).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                PlayerTimer.tick(p, campaign, pt.number);
            }
        }, 0, 1);
        this.isLocked = true;
    }

    public void startTimerFor(Player p, @NotNull Campaign campaign, @NotNull Point pt) {
        startTimerFor(p, campaign, pt, 0L);
    }

    public void stopTimerFor(Player p) {
        var timer = PlayerTimer.getTimer(p);
        if (timer != null) timer.cancel();
        this.isLocked = false;
    }
}
