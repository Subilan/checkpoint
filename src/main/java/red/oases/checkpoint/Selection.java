package red.oases.checkpoint;

import org.bukkit.configuration.MemoryConfiguration;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Selection {
    public static MemoryConfiguration memory = new MemoryConfiguration();

    /**
     * 查看当前的选择状态
     *
     * @param actionIdentifier 选择标识
     * @return 0 - 尚未选择；1 - 选择了一个点；2 - 选择了两个点
     */
    public static int getState(String actionIdentifier) {
        var list = memory.getIntegerList(actionIdentifier + ".value1");
        if (list.isEmpty()) return 0;
        list = memory.getIntegerList(actionIdentifier + ".value2");
        if (list.isEmpty()) return 1;
        return 2;
    }

    public static List<Integer> getData(String actionIdentifier, Integer stage) {
        return memory.getIntegerList(actionIdentifier + ".value" + stage.toString());
    }

    public static void clear(String actionIdentifier) {
        memory.set(actionIdentifier, null);
    }

    public static void create(String actionIdentifier, int x, int y, int z) {
        switch (getState(actionIdentifier)) {
            case 0 -> memory.set(actionIdentifier + ".value1", List.of(x, y, z));
            case 1, 2 -> memory.set(actionIdentifier + ".value2", List.of(x, y, z));
        }
    }

    public static void build(String playername, String actionIdentifier, String path) {
        var list1 = memory.getIntegerList(actionIdentifier + ".value1");
        var list2 = memory.getIntegerList(actionIdentifier + ".value2");
        Files.selections.set(path + ".pos1", list1);
        Files.selections.set(path + ".pos2", list2);
        Files.selections.set(path + ".creator", playername);
        Files.selections.set(path + ".created_at", new Date().getTime());
        Files.saveSelections();
        clear(actionIdentifier);
    }
}
