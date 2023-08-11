package red.oases.checkpoint;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Date;
import java.util.List;

public class Campaign {
    public ConfigurationSection section;
    private final String name;
    public String targetTrack;
    public long createdAt;
    public String createdBy;
    public boolean isOpen;

    public Campaign(String cam) {
        this.name = cam;
        this.section = Files.campaigns.getConfigurationSection(cam);
        this.fields();
    }

    private void fields() {
        if (section == null) return;
        this.targetTrack = section.getString("target_track");
        this.createdAt = section.getLong("created_at");
        this.createdBy = section.getString("created_by");
        this.isOpen = section.getBoolean("is_open");
    }

    public Campaign create(String track, CommandSender sender) {
        this.section.set("target_track", track);
        this.section.set("created_at", new Date().getTime());
        this.section.set("created_by", sender);
        this.section.set("is_open", false);
        Files.saveCampaigns();
        this.fields();
        return this;
    }

    public void delete() {
        Files.campaigns.set(name, null);
        Files.saveCampaigns();
        this.fields();
    }

    public void setStatus(String status) {
        this.section.set("is_open", status.equals("open"));
        Files.saveCampaigns();
        this.fields();
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

    public List<String> getPlayers() {
        return this.section.getStringList("players");
    }
}
