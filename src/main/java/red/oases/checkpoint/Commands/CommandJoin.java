package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Config;
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

        ProgressUtils.enableCampaignFor(p);
        LogUtils.send("你已成功参赛。", sender);
        var defaultCam = Config.getString("default-campaign-name");

        if (defaultCam == null) {
            var campaigns = CommonUtils.getCampaignNames().stream().toList();
            if (campaigns.isEmpty()) {
                LogUtils.send("暂无赛道可供选择。", sender);
                return true;
            }
            defaultCam = campaigns.get(new Random(campaigns.size()).nextInt());
        }
        LogUtils.send("默认为你选择的赛道为 " + defaultCam + "。", sender);
        LogUtils.send("如需切换，请使用 /cpt switch 指令。", sender);

        return true;
    }
}
