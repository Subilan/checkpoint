package red.oases.checkpoint.Objects;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import red.oases.checkpoint.Extra.Exceptions.ObjectNotFoundException;
import red.oases.checkpoint.Utils.FileUtils;

import java.util.Date;
import java.util.List;

public class Campaign {
    public ConfigurationSection section;
    private final String name;
    public boolean isFinished = false;

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

    public void addPlayer(String playername) {
        var list = this.section.getStringList("players");
        list.add(playername);
        this.section.set("players", list);
        FileUtils.saveCampaigns();
    }

    public void removePlayer(String playername) {
        var list = this.section.getStringList("players");
        list.remove(playername);
        this.section.set("players", list);
        FileUtils.saveCampaigns();
    }

    public static List<String> getPlayers(String cam) {
        var section = FileUtils.campaigns.getConfigurationSection(cam);

        if (section == null) return List.of();

        return section.getStringList("players");
    }

    public Track getTrack() {
        return new Track(getTrackName());
    }

    public void setFinished() {
        this.isFinished = true;
    }
}
