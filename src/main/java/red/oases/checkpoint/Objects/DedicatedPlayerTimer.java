package red.oases.checkpoint.Objects;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import red.oases.checkpoint.Utils.LogUtils;

import java.util.TimerTask;

public class DedicatedPlayerTimer {
    public Player player;
    public boolean isLocked = false;

    public static void unlock(Player p) {
        PlayerTimer.getDedicated(p).isLocked = false;
    }

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
                if (PlayerTimer.getTick(p, campaign, pt.number) > Config.getTimerMaxTimeout() * 1000L) {
                    Logic.reset(p, campaign);
                    LogUtils.sendWithoutPrefix(
                            LogUtils.t("由于在单一区间内停留超过 " + Config.getTimerMaxTimeout() + " 秒，本场比赛数据已重置。", NamedTextColor.RED),
                            p
                    );
                    this.cancel();
                }
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
