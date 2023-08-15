package red.oases.checkpoint.Objects;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Extra.Exceptions.ObjectNotFoundException;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.FileUtils;

import java.util.Date;
import java.util.List;

public class Campaign {
    public ConfigurationSection section;
    private final String name;
    public boolean isFinished = false;

    public static @Nullable Campaign of(Player p) {
        for (var cam : CommonUtils.getCampaignNames()) {
            var players = FileUtils.campaigns.getStringList(cam + ".players");
            if (players.contains(p.getName())) return new Campaign(cam);
        }

        return null;
    }

    public static @Nullable Campaign of(String playername) {
        for (var cam : CommonUtils.getCampaignNames()) {
            var players = FileUtils.campaigns.getStringList(cam + ".players");
            if (players.contains(playername)) return new Campaign(cam);
        }

        return null;
    }

    public String getName() {
        return this.name;
    }

    public String getTrackName() {
        return section.getString("target_track");
    }

    public Date getCreatedAt() {
        return new Date(section.getLong("created_at"));
    }

    public String getCreatedBy() {
        return section.getString("created_by");
    }

    public boolean isOpen() {
        return section.getBoolean("is_open");
    }

    public List<String> getFinishedPlayers() {
        return section.getStringList("finished_players");
    }

    public boolean isFinished(Player p) {
        return getFinishedPlayers().contains(p.getName());
    }

    public Campaign(String cam, Boolean nocheck) {
        this.name = cam;
        this.section = FileUtils.campaigns.getConfigurationSection(cam);
        if (section == null) {
            if (nocheck) {
                this.section = FileUtils.campaigns.createSection(cam);
            } else {
                throw new ObjectNotFoundException("campaign");
            }
        }
    }

    public Campaign(String cam) {
        this.name = cam;
        this.section = FileUtils.campaigns.getConfigurationSection(cam);
        if (section == null) throw new ObjectNotFoundException("campaign");
    }

    public static Campaign create(String name, String track, CommandSender sender) {
        var campaign = new Campaign(name, true);
        campaign.section.set("target_track", track);
        campaign.section.set("created_at", new Date().getTime());
        campaign.section.set("created_by", sender.getName());
        campaign.section.set("is_open", false);
        FileUtils.saveCampaigns();
        return campaign;
    }

    public void delete() {
        FileUtils.campaigns.set(name, null);
        FileUtils.saveCampaigns();
    }

    public void setStatus(String status) {
        this.section.set("is_open", status.equals("open"));
        FileUtils.saveCampaigns();
    }

    public void addPlayer(Player p) {
        var list = this.section.getStringList("players");
        list.add(p.getName());
        this.section.set("players", list);
        FileUtils.saveCampaigns();
    }

    /**
     * 从参赛名单中去除一位玩家，同时删除其存在的竞赛数据。
     * @param p 玩家对象
     */
    public void removePlayer(Player p) {
        var list = this.section.getStringList("players");
        list.remove(p.getName());
        this.section.set("players", list);
        this.unsetFinished(p);
        FileUtils.saveCampaigns();
    }

    public static List<String> getPlayers(String cam) {
        var section = getSection(cam);

        if (section == null) return List.of();

        return section.getStringList("players");
    }

    public static ConfigurationSection getSection(String name) {
        return FileUtils.campaigns.getConfigurationSection(name);
    }

    public ConfigurationSection getSection() {
        return getSection(this.name);
    }

    public Track getTrack() {
        return new Track(getTrackName());
    }

    /**
     * 将玩家的状态设置为已完成竞赛。
     * @param p 玩家对象
     */
    public void setFinished(Player p) {
        var list = getSection().getStringList("finished_players");
        list.add(p.getName());
        getSection().set("finished_players", list);
        FileUtils.saveCampaigns();
    }

    /**
     * 将玩家的状态设置为未完成竞赛。
     * @param p 玩家对象
     */
    public void unsetFinished(Player p) {
        var list = getSection().getStringList("finished_players");
        list.remove(p.getName());
        getSection().set("finished_players", list);
        FileUtils.saveCampaigns();
    }
}
