package kami.gg.souppvp.util.menu;

import kami.gg.souppvp.SoupPvP;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager {

    private final SoupPvP plugin;
    private final Map<UUID, Menu> menus;

    public MenuManager(SoupPvP plugin) {
        this.plugin = plugin;
        this.menus = new HashMap<>();
        new MenuListener(this);
    }

}
