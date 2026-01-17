package kami.gg.souppvp.perk.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.perk.menu.button.*;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PerksMenu extends Menu {

    private static final int[] PERK_SLOTS = {11, 13, 15};

    @Override
    public String getTitle(Player player) {
        return "Perk Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile == null) {
            return buttons;
        }

        buttons.put(0, new InfoPerksButton());
        for (int i = 0; i < PERK_SLOTS.length; i++) {
            int slotNumber = i + 1;
            int guiSlot = PERK_SLOTS[i];

            buttons.put(guiSlot, new PerkSlotButton(slotNumber));
        }

        setPlaceholder(true);
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }
}