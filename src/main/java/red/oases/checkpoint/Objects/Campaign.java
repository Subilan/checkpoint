package red.oases.checkpoint.Objects;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Extra.Exceptions.ObjectNotFoundException;
import red.oases.checkpoint.Utils.AnalyticUtils;
import red.oases.checkpoint.Utils.CommonUtils;
import red.oases.checkpoint.Utils.FileUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Campaign {
    private final String name;

    public static boolean isPresent(String cam) {
        return CommonUtils.getCampaignNames().contains(cam);
    }

    /**
     * 判断是否该玩家目前参与了至少一场比赛
     * @param p 玩家
     * @return 是否参与
     */
    public static boolean any(Player p) {
        return !Campaign.get(p).isEmpty();
    }

    public static List<Campaign> get(Player p) {
        return CommonUtils.getCampaignNames().stream().filter(c -> {
            var players = FileUtils.campaigns.getStringList(c + ".players");
            return players.contains(p.getName());
        }).map(Campaign::new).toList();
    }

    @Deprecated
    public static @Nullable Campaign of(Player p) {
        for (var cam : CommonUtils.getCampaignNames()) {
            var players = FileUtils.campaigns.getStringList(cam + ".players");
            if (players.contains(p.getName())) return new Campaign(cam);
        }

        return null;
    }

    @Deprecated
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
        return getSection().getString("target_track");
    }

    public Date getCreatedAt() {
        return new Date(getSection().getLong("created_at"));
    }

    public String getCreatedBy() {
        return getSection().getString("created_by");
    }

    public List<@NotNull Analytics> getAnalytics() {
        var result = new ArrayList<Analytics>();
        var sec = AnalyticUtils.getSection(this.getName());
        if (sec == null) return List.of();
        for (var k : sec.getKeys(false)) {
            var an = Analytics.of(this, sec.getString(k + ".player"));
            if (an == null) continue;
            result.add(an);
        }
        return result;
    }

    public String getStatus() {
        return getSection().getString("status");
    }

    public boolean isOpen() {
        return Objects.equals(getSection().getString("status"), "open");
    }

    public boolean isPrivate() {
        return Objects.equals(getSection().getString("status"), "private");
    }

    public Campaign(@Nullable String cam, Boolean nocheck) {
        if (cam == null) throw new ObjectNotFoundException("name is null");
        this.name = cam;
        var section = FileUtils.campaigns.getConfigurationSection(cam);
        if (section == null) {
            if (nocheck) {
                FileUtils.campaigns.createSection(cam);
                FileUtils.saveCampaigns();
            } else {
                throw new ObjectNotFoundException("campaign");
            }
        }
    }

    public Campaign(@Nullable String cam) {
        if (cam == null) throw new ObjectNotFoundException("name is null");
        this.name = cam;
        var section = FileUtils.campaigns.getConfigurationSection(cam);
        if (section == null) throw new ObjectNotFoundException("campaign");
    }

    public static Campaign create(String name, String track, CommandSender sender) {
        var campaign = new Campaign(name, true);
        campaign.getSection().set("target_track", track);
        campaign.getSection().set("created_at", new Date().getTime());
        campaign.getSection().set("created_by", sender.getName());
        campaign.getSection().set("status", "close");
        FileUtils.saveCampaigns();
        return campaign;
    }

    public void delete() {
        FileUtils.campaigns.set(name, null);
        FileUtils.saveCampaigns();
    }

    public void setStatus(String status) {
        this.getSection().set("status", status);
        FileUtils.saveCampaigns();
    }

    public void addPlayer(Player p) {
        var list = this.getSection().getStringList("players");
        if (!list.contains(p.getName())) list.add(p.getName());
        this.getSection().set("players", list);
        FileUtils.saveCampaigns();
    }

    public List<String> getPlayers() {
        return this.getSection().getStringList("players");
    }

    /**
     * 从参赛名单中去除一位玩家，同时删除其存在的竞赛数据。
     * @param p 玩家对象
     */
    public void removePlayer(Player p) {
        var list = this.getSection().getStringList("players");
        list.remove(p.getName());
        this.getSection().set("players", list);
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
}
