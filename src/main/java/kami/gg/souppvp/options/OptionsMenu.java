package kami.gg.souppvp.options;

import kami.gg.souppvp.options.button.*;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class OptionsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Configure your settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(10, new KillDeathMessagesButton());
        buttons.put(11, new KillstreakMessagesButton());
        buttons.put(12, new ScoreboardButton());
        buttons.put(13, new EasySoupButton());
        buttons.put(14, new ResetStatisticsButton(10000));
        setPlaceholder(true);
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }

}
