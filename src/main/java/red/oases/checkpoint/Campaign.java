package red.oases.checkpoint;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import red.oases.checkpoint.Extra.Exceptions.ObjectNotFoundException;

import java.util.Date;
import java.util.List;

public class Campaign {
    public ConfigurationSection section;
    private final String name;
    public String getName() {
        return this.name;
    }

    public String getTargetTrack() {
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


    public Campaign(String cam) {
        this.name = cam;
        this.section = Files.campaigns.getConfigurationSection(cam);
        if (section == null) throw new ObjectNotFoundException("campaign");
    }

    public Campaign create(String track, CommandSender sender) {
        this.section.set("target_track", track);
        this.section.set("created_at", new Date().getTime());
        this.section.set("created_by", sender);
        this.section.set("is_open", false);
        Files.saveCampaigns();
        return this;
    }

    public void delete() {
        Files.campaigns.set(name, null);
        Files.saveCampaigns();
    }

    public void setStatus(String status) {
        this.section.set("is_open", status.equals("open"));
        Files.saveCampaigns();
    }

    public void addPlayer(String playername) {
        var list = this.section.getStringList("players");
        list.add(playername);
        this.section.set("players", list);
        Files.saveCampaigns();
    }

    public void removePlayer(String playername) {
        var list = this.section.getStringList("players");
        list.remove(playername);
        this.section.set("players", list);
        Files.saveCampaigns();
    }

    public static List<String> getPlayers(String cam) {
        var section = Files.campaigns.getConfigurationSection(cam);

        if (section == null) return List.of();

        return section.getStringList("players");
    }

    public Track getTrack() {
        return new Track(getTargetTrack());
    }
}
