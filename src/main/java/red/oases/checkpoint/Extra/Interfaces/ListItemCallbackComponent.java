package red.oases.checkpoint.Extra.Interfaces;

import net.kyori.adventure.text.Component;

@FunctionalInterface
public interface ListItemCallbackComponent {
    Component getComponent(int index);
}
