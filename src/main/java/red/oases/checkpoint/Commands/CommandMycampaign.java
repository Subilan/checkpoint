package red.oases.checkpoint.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import red.oases.checkpoint.Extra.Annotations.DisableConsole;
import red.oases.checkpoint.Extra.Annotations.PermissionLevel;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Utils.LogUtils;

@DisableConsole
@PermissionLevel(0)
public class CommandMycampaign extends Command {
    public CommandMycampaign(String[] args, CommandSender sender) {
        super(args, sender);
    }

    @Override
    protected boolean execute() {
        var p = (Player) sender;

        var campaign = Campaign.of(p);

        if (campaign == null) {
            LogUtils.send("你还没有加入竞赛。", sender);
            return true;
        }

        var string = String.format("""
                \n竞赛信息
                
                竞赛名称 - %s
                参与人数 - %s
                路径点总数 - %d
                """,
                campaign.getName(),
                campaign.getPlayers().size(),
                campaign.getTrack().getPoints().size());
        var finished = campaign.getFinishedPlayers().size();

        if (finished > 0) string += "\n已经有 " + finished + " 名玩家完成了比赛！";
        if (campaign.isFinished((Player) sender)) string += "\n你已经完成了比赛！";
        LogUtils.send(string, sender);
        return true;
    }
}
