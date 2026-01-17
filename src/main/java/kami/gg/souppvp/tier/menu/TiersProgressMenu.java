package kami.gg.souppvp.tier.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.button.TierProgressButton;
import kami.gg.souppvp.tier.button.ViewTiersButton;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TiersProgressMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Tier Progress";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        buttons.put(13, new TierProgressButton(profile));
        buttons.put(31, new ViewTiersButton());

        if (profile.getTier().getNext() != null) {
            int[] BAR_SLOTS = {20, 21, 22, 23, 24};
            addProgressBar(buttons, BAR_SLOTS, profile.getExperiences(), profile.getTier().getNext().getRequiredExperiences());
        }

        setPlaceholder(true);
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 45;
    }

    private void addProgressBar(Map<Integer, Button> buttons, int[] slots, int currentXP, int requiredXP) {
        double progress = Math.min(1.0, (double) currentXP / requiredXP);
        int filled = (int) Math.floor(progress * slots.length);

        for (int i = 0; i < slots.length; i++) {
            short color;

            if (i < filled) {
                color = 5;
            } else if (i == filled && progress > 0) {
                color = 4;
            } else {
                color = 14;
            }

            buttons.put(slots[i], new GlassFillerButton(color));
        }
    }

    public static class GlassFillerButton extends Button {

        private final short data;

        public GlassFillerButton(short data) {
            this.data = data;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .durability(data)
                    .name(" ")
                    .build();
        }
    }
}