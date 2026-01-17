package kami.gg.souppvp.tier.menu;

import kami.gg.souppvp.tier.Tiers;
import kami.gg.souppvp.tier.button.TierButton;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TiersListMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "All Available Tiers";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int[] tierSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };

        buttons.put(44, new BackButton(new TiersProgressMenu()));

        int index = 0;
        for (Tiers tier : Tiers.values()) {
            if (index >= tierSlots.length) break;
            buttons.put(tierSlots[index++], new TierButton(tier));
        }

        setPlaceholder(true);
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 45;
    }
}
