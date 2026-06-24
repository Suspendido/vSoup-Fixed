package kami.gg.souppvp.tier.button;

import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TierProgressButton extends Button {

    private final Profile profile;

    public TierProgressButton(Profile profile) {
        this.profile = profile;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        int currentTier = profile.getTier();
        int nextTier = currentTier + 1;
        TierCategory category = TierCategory.getCategoryByLevel(currentTier);
        TierCategory nextCategory = TierCategory.getCategoryByLevel(nextTier);

        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add("&b┃ &fCurrent Tier: &b" + currentTier);
        lore.add("&b┃ &fCategory: " + category.getColor() + category.getName());
        lore.add("&b┃ &fExperiences: &b" + profile.getExperiences());
        lore.add("");
        lore.add("&b┃ &fNext Tier: &b" + nextTier);
        lore.add("&b┃ &fNext Category: " + nextCategory.getColor() + nextCategory.getName());
        lore.add("");
        lore.add("&b┃ &fReward for next tier:");
        lore.add("&7- &b" + calculateTierReward(nextTier) + " credits");

        return new ItemBuilder(Material.EXP_BOTTLE)
                .name("&bTier Progress")
                .lore(lore)
                .build();
    }

    private int calculateTierReward(int tier) {
        // Fórmula de rewards basada en el sistema original
        // Tier 1: 500, 2: 500, 3: 1000, etc.
        if (tier == 0) return 0;
        if (tier <= 5) return 500;
        if (tier <= 10) return 1000;
        if (tier <= 15) return 2000;
        if (tier <= 20) return 3000;
        return 5000; // Para niveles más altos
    }
}
