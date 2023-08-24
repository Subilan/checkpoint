package red.oases.checkpoint.Commands.PlayerCommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Commands.Command;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.DisplayMap;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.PointUtils;
import red.oases.checkpoint.Utils.ProgressUtils;

import java.util.HashMap;

@DisableConsole
@PermissionLevel(0)
public class CommandMycampaign extends Command {
    public CommandMycampaign(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        var p = (Player) sender;

        var campaigns = Campaign.get(p);

        if (campaigns.isEmpty()) {
            LogUtils.send("你还没有加入任何竞赛。", sender);
            return true;
        }

        for (var campaign : campaigns) {
            var map = new HashMap<String, Component>();
            map.put("竞赛名称", LogUtils.t(campaign.getName(), NamedTextColor.YELLOW));
            map.put("参与人数", LogUtils.t(String.valueOf(campaign.getPlayers().size()), NamedTextColor.YELLOW));
            map.put("路径点总数", LogUtils.t(String.valueOf(campaign.getTrack().getPoints().size()), NamedTextColor.YELLOW));
            map.put("记录点总数", LogUtils.t(String.valueOf(PointUtils.getAllCheckpoints(campaign).size()), NamedTextColor.YELLOW));
            map.put("当前竞赛状态", campaign.isOpen()
                    ? LogUtils.t("开启", NamedTextColor.GREEN)
                    : (campaign.isPrivate()
                                    ? LogUtils.t("私有", NamedTextColor.LIGHT_PURPLE)
                                    : LogUtils.t("关闭", NamedTextColor.RED)));

            var finished = ProgressUtils.getFinishedPlayers(campaign);

            if (!finished.isEmpty()) map.put("已完成人数", LogUtils.t(String.valueOf(finished.size()), NamedTextColor.LIGHT_PURPLE));

            new DisplayMap(
                    LogUtils.t(campaign.getName()).color(NamedTextColor.YELLOW)
                            .appendSpace()
                            .append(LogUtils.t("的竞赛信息").color(NamedTextColor.AQUA)),
                    sender,
                    map
            ).send();
        }

        return true;
    }
}
