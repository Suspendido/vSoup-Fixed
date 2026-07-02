package kami.gg.souppvp.profile.menus.options;

import kami.gg.souppvp.profile.menus.options.button.*;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class OptionsMenu extends Menu {

    public OptionsMenu(Player player) {
        super(player, "Configure your settings", 27, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(10, new KillDeathMessagesButton());
        buttons.put(11, new KillstreakMessagesButton());
        buttons.put(12, new ScoreboardButton());
        buttons.put(13, new EasySoupButton());
        buttons.put(14, new ResetStatisticsButton(10000));
        setFillEnabled(true);
        return buttons;
    }
}
