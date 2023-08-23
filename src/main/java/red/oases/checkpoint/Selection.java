package red.oases.checkpoint;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Selection {
    public static MemoryConfiguration memory = new MemoryConfiguration();
    public static Map<String, Integer> states = new HashMap<>();

    /**
     * 查看当前的选择状态
     *
     * @param actionIdentifier 选择标识
     * @return 0 - 尚未选择；1 - 选择了一个点；2 - 选择了两个点
     */
    public static Integer getState(String actionIdentifier) {
        var res = states.get(actionIdentifier);
        return res == null ? 0 : res;
    }

    public static List<Integer> getData(String actionIdentifier, Integer stage) {
        return memory.getIntegerList(actionIdentifier + ".value" + stage.toString());
    }

    public static void clear(String actionIdentifier) {
        memory.set(actionIdentifier, null);
    }

    public static void create(String actionIdentifier, int x, int y, int z) {
        switch (getState(actionIdentifier)) {
            case 0, 2 -> {
                memory.set(actionIdentifier + ".value1", List.of(x, y, z));
                states.remove(actionIdentifier);
                states.put(actionIdentifier, 1);
            }
            case 1 -> {
                memory.set(actionIdentifier + ".value2", List.of(x, y, z));
                states.remove(actionIdentifier);
                states.put(actionIdentifier, 2);
            }
        }
    }
}
