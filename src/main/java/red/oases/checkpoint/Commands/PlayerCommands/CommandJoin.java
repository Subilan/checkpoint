package red.oases.checkpoint.Commands.PlayerCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.Config;
import red.oases.checkpoint.Objects.Logic;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.ProgressUtils;

import java.util.Random;

@PermissionLevel(0)
@DisableConsole
public class CommandJoin extends Command {
    public CommandJoin(String[] args, CommandSender sender) {
        super(args, sender);
    }

    protected boolean execute() {
        var p = (Player) sender;

        if (ProgressUtils.HasCampaignEnabled(p)) {
            LogUtils.send("你已参赛。", sender);
            return true;
        }

        var defaultCam = Config.getString("default-campaign-name");

        if (defaultCam == null) {
            // 只考虑 open 状态的比赛
            var campaigns = CommonUtils.getCampaignNames()
                    .stream()
                    .filter(cam -> new Campaign(cam).isOpen())
                    .toList();
            if (campaigns.isEmpty()) {
                LogUtils.send("参赛失败，暂无竞赛可供选择。", sender);
                return true;
            }
            defaultCam = campaigns.get(new Random().nextInt(campaigns.size()));
        }
        LogUtils.send("你已成功参赛。", sender);
        LogUtils.send("默认为你选择的竞赛为 " + defaultCam + "。", sender);
        LogUtils.send("如需切换，请使用 /cpt switch 指令。", sender);

        Logic.join(p, new Campaign(defaultCam));

        return true;
    }
}
