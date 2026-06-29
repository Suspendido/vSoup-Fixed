package kami.gg.souppvp.perk.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.perk.menu.button.PerkToggleButton;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PerkToggleMenu extends Menu {

    public PerkToggleMenu(Player player) {
        super(player, "Toggle Perks", 36, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            buttons.putIfAbsent(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }

        int slot = 9;
        for (Perk perk : SoupPvP.getInstance().getPerksHandler().getPerks()) {
            if (slot >= 54) break;
            buttons.put(slot, new PerkToggleButton(perk));
            slot++;
        }

        setFillEnabled(true);
        return buttons;
    }
}
