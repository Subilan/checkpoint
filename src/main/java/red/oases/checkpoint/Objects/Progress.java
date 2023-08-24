package red.oases.checkpoint.Objects;

import org.bukkit.entity.Player;
import red.oases.checkpoint.Utils.FileUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Progress {

    public Map<Player, Map.Entry<Point, Long>> pointHistory = new HashMap<>();
    private Campaign campaign;
    private Point point;
    public Player p;

    public Progress(Player p) {
        this.p = p;
    }
    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public void unsetCampaign(Campaign campaign) {
        this.campaign = null;
    }

    /**
     * 在玩家进度记录中添加该点，并将当前 point 指向最新的点。
     * @param pt 点
     */
    public void updatePoint(Point pt) {
        this.point = pt;
        this.pointHistory.put(p, Map.entry(pt, new Date().getTime()));
    }

    /**
     * 获取玩家所通过的最近的一个点
     * @return 点对象
     */
    public Point getPoint() {
        return this.point;
    }

    /**
     * 获取玩家当前所在的竞赛
     * @return 竞赛对象
     */
    public Campaign getCampaign() {
        return this.campaign;
    }

}
