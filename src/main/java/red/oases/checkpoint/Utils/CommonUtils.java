package red.oases.checkpoint.Utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import red.oases.checkpoint.Objects.Campaign;
import red.oases.checkpoint.Objects.PlayerTimer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class CommonUtils {
    public static Set<String> getCampaignNames() {
        FileUtils.reload();
        var section = FileUtils.campaigns;

        if (section == null) {
            return new HashSet<>();
        } else {
            return section.getKeys(false);
        }
    }

    public static Set<String> getTrackNames() {
        FileUtils.reload();
        var section = FileUtils.selections.getConfigurationSection("data");

        if (section == null) {
            return new HashSet<>();
        } else {
            return section.getKeys(false);
        }
    }


    /**
     * 获得指向指定 path 的别名
     *
     * @param path 指定 path，不带 data. 前缀
     * @return 指定别名。如果不存在，返回 ""
     */
    public static String getAliasByPath(String path) {
        var result = new AtomicReference<>("");

        var aliasSection = FileUtils.selections.getConfigurationSection("aliases");

        if (aliasSection != null) {
            var map = aliasSection.getValues(false);
            map.forEach((key, value) -> {
                if (value.equals("data." + path)) result.set(key);
            });
        }

        return result.get();
    }

    public static @Nullable String getPathByAlias(String alias) {
        return FileUtils.selections.getString(String.format("aliases.%s", alias));
    }

    /**
     * 转换对应字符串为非负整数。如果转换失败，返回 0。
     *
     * @param target 待转换的字符串
     * @return 成功为对应整数，不成功为 0
     */
    public static int mustPositive(String target) {
        int result;
        try {
            result = Integer.parseInt(target);
        } catch (NumberFormatException e) {
            return 0;
        }
        return result;
    }

    public static void cleanCampaignFor(Player p) {
        PlayerTimer.reset(p);
        var campaign = Campaign.of(p);
        assert campaign != null;
        campaign.unsetFinished(p);
        AnalyticUtils.removeCampaignResult(p);
    }
}
