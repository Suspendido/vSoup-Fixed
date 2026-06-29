package kami.gg.souppvp.perk.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.perk.menu.button.*;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AllPerksMenu extends Menu {

    private static final Map<UUID, Integer> selectedSlots = new HashMap<>();

    public AllPerksMenu(Player player) {
        super(player, "Perks", 36, true);
    }

    public static int getSelectedSlot(Player player) {
        return selectedSlots.getOrDefault(player.getUniqueId(), 0);
    }

    public static void setSelectedSlot(Player player, int slot) {
        selectedSlots.put(player.getUniqueId(), slot);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(3, new SlotCycleButton());
        buttons.put(5, new InfoPerksButton());

        for (int i = 0; i < 9; i++) {
            buttons.putIfAbsent(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }

        int slot = 9;
        for (Perk perk : SoupPvP.getInstance().getPerksHandler().getPerks()) {
            if (slot >= 36) break;
            if (SoupPvP.getInstance().getPerksHandler().getDisabledPerks().contains(perk.getName())) continue;
            buttons.put(slot, new PerkButton(perk));
            slot++;
        }

        return buttons;
    }
}