package kami.gg.souppvp.events.menu;

import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.menu.button.EventButton;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HostEventsMenu extends Menu {

    public HostEventsMenu(Player player) {
        super(player, "Select an event to host", 45, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> button = new HashMap<>();

        button.put(10, new EventButton(EventType.SUMO));
        button.put(13, new EventButton(EventType.TNTTAG));

        button.put(16, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, " "));
        button.put(28, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, " "));
        button.put(31, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, " "));
        button.put(34, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, " "));

        setFillEnabled(true);
        return button;
    }
}
