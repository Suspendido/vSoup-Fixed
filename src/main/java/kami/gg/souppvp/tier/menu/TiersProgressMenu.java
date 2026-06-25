package kami.gg.souppvp.tier.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.util.TierUtils;
import kami.gg.souppvp.tier.button.*;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TiersProgressMenu extends Menu {

    public TiersProgressMenu(Player player) {
        super(player, "Tier Progress", 54, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        buttons.put(10, new CurrentTierButton(profile));
        buttons.put(43, new NextTierButton(profile));
        buttons.put(49, new TierIconSelectorButton(profile));

        int[] BAR_SLOTS = {19, 28, 37, 38, 39, 30, 21, 12, 13, 14, 23, 32, 41, 42};
        int nextTierXP = TierUtils.calculateNextTierXP(profile.getTier());
        addProgressBar(buttons, BAR_SLOTS, profile.getExperiences(), nextTierXP);

        setFillEnabled(true);
        return buttons;
    }

    private void addProgressBar(Map<Integer, Button> buttons, int[] slots, int currentXP, int requiredXP) {
        double progress = Math.min(1.0, (double) currentXP / requiredXP);
        int filled = (int) Math.floor(progress * slots.length);
        double xpPerSlot = (double) requiredXP / slots.length;

        for (int i = 0; i < slots.length; i++) {
            short color;
            int slotXP = (int) Math.ceil((i + 1) * xpPerSlot);
            boolean isFilled = i < filled;
            boolean isPartial = i == filled && progress > 0;

            if (isFilled) {
                color = 10;
            } else if (isPartial) {
                color = 11;
            } else {
                color = 8;
            }

            buttons.put(slots[i], new ProgressItemButton(color, isFilled, isPartial, slotXP, currentXP));
        }
    }

    public static class ProgressItemButton extends Button {

        private final short data;
        private final boolean filled;
        private final boolean partial;
        private final int slotXP;
        private final int currentXP;

        public ProgressItemButton(short data, boolean filled, boolean partial, int slotXP, int currentXP) {
            this.data = data;
            this.filled = filled;
            this.partial = partial;
            this.slotXP = slotXP;
            this.currentXP = currentXP;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemBuilder builder = new ItemBuilder(Material.INK_SACK)
                    .durability(data)
                    .name(" ");

            if (filled) {
                builder.name("&a&l" + slotXP + " Exp")
                       .lore("&aComplete!");
            } else if (partial) {
                int remaining = slotXP - currentXP;
                builder.name("&e&l" + remaining)
                       .lore("&7Need &e" + remaining + " &7more exp");
            } else {
                int remaining = slotXP - currentXP;
                builder.name("&7&l" + slotXP)
                       .lore("&7Need &d" + remaining + " &7more exp");
            }

            return builder.build();
        }
    }
}