package red.oases.checkpoint.Commands.PlayerCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Config;
import red.oases.checkpoint.Objects.PlayerTimer;
import red.oases.checkpoint.Objects.Progress;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.ProgressUtils;
import red.oases.checkpoint.Utils.SoundUtils;

@DisableConsole
@PermissionLevel(0)
public class CommandResume extends Command {
    public CommandResume(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {

        if (!Config.getAllowResume()) {
            LogUtils.send("此指令已被禁用。", sender);
            return true;
        }

        var p = (Player) sender;

        if (!ProgressUtils.isHalfway(p)) {
            LogUtils.send("你已完成或还未开始此比赛。。", sender);
            return true;
        }

        var running = Progress.getRunningCampaign(p);
        assert running != null;

        if (!running.isOpen()) {
            LogUtils.send("继续失败：比赛已关闭。", sender);
            return true;
        }

        if (Progress.isPauseExpired(p)) {
            LogUtils.send("继续失败：数据已过期。", sender);
            return true;
        }

        if (!Progress.isPaused(p, running)) {
            LogUtils.send("继续失败：你没有暂停此比赛。", sender);
            return true;
        }

        var pt = Progress.getPoint(p);
        assert pt != null;

        Progress.setPaused(p, running, false);
        PlayerTimer.retrieveTicks(p, running);
        PlayerTimer.getDedicated(p).startTimerFor(
                p, running, pt, PlayerTimer.getLastTick(p)
        );
        SoundUtils.playSoundB(p);
        LogUtils.send("比赛 " + running.getName() + " 计时已重新开始", sender);
        LogUtils.send("当前通过第 " + pt.number + " 个点", sender);
        LogUtils.send("本段用时 " + CommonUtils.millisecondsToReadable(PlayerTimer.getTick(p, running, pt.number)), sender);
        LogUtils.send("总计用时 " + CommonUtils.millisecondsToReadable(PlayerTimer.getTotalTime(p, running)), sender);
        return true;
    }
}
