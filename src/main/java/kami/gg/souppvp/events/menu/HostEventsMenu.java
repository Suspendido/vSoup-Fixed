package kami.gg.souppvp.events.menu;

import kami.gg.souppvp.events.menu.button.*;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HostEventsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.t("Select an event to host");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        buttonMap.put(10, new SumoEventButton());
        buttonMap.put(13, new TNTTagEventButton());
        buttonMap.put(16, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, " "));
        buttonMap.put(28, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, " "));
        buttonMap.put(31, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, " "));
        buttonMap.put(34, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, " "));
        setPlaceholder(true);
        return buttonMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 45;
    }
}
