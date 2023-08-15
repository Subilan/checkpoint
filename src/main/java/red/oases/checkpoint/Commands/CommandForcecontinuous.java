package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.FileUtils;
import red.oases.checkpoint.Utils.LogUtils;

public class CommandForcecontinuous extends Command {
    public CommandForcecontinuous(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        if (args.length < 2) {
            LogUtils.send("参数不足：/cpt forcecontinuous <track>", sender);
            return true;
        }

        var track = args[1];

        if (!CommonUtils.getTrackNames().contains(track)) {
            LogUtils.send("对应赛道不存在。", sender);
            return true;
        }

        var section = FileUtils.selections.getConfigurationSection("data." + track);
        assert section != null;
        var shouldBe = 1;
        for (var k : section.getKeys(false)) {
            var num = CommonUtils.mustPositive(k);
            if (num == shouldBe) {
                shouldBe++;
                continue;
            }
            section.set(Integer.toString(shouldBe), section.getConfigurationSection(k));
            section.set(k, null);
            shouldBe++;
        }

        LogUtils.send("成功整理相应赛道中路径点的序号。", sender);
        return true;
    }
}
