package red.oases.checkpoint.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.DisplayMap;
import red.oases.checkpoint.Utils.LogUtils;
import red.oases.checkpoint.Utils.PointUtils;

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
            map.put("竞赛名称", LogUtils.t(campaign.getName()));
            map.put("参与人数", LogUtils.t(String.valueOf(campaign.getPlayers().size())));
            map.put("路径点总数", LogUtils.t(String.valueOf(campaign.getTrack().getPoints().size())));
            map.put("记录点总数", LogUtils.t(String.valueOf(PointUtils.getAllCheckpoints(campaign).size())));
            map.put("当前竞赛状态", campaign.isOpen()
                    ? LogUtils.t("开启", NamedTextColor.GREEN)
                    : (campaign.isPrivate()
                                    ? LogUtils.t("私有", NamedTextColor.LIGHT_PURPLE)
                                    : LogUtils.t("关闭", NamedTextColor.RED)));

            var finished = campaign.getFinishedPlayers().size();

            if (finished > 0) map.put("已完成人数", LogUtils.t(String.valueOf(finished)));

            new DisplayMap(
                    LogUtils.t(p.getName()).color(NamedTextColor.YELLOW)
                            .appendSpace()
                            .append(LogUtils.t("参加的竞赛信息").color(NamedTextColor.AQUA)),
                    sender,
                    map
            ).send();
        }

        var content = LogUtils.t("共参与了", NamedTextColor.GREEN)
                .appendSpace()
                .append(LogUtils.t(String.valueOf(campaigns.size()), NamedTextColor.YELLOW))
                .appendSpace()
                .append(LogUtils.t("场竞赛", NamedTextColor.GREEN))
                .appendNewline().appendNewline()
                .append(LogUtils.t("竞赛名称 - "));

        return true;
    }
}
