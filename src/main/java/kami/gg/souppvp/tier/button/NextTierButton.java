package kami.gg.souppvp.tier.button;

import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.tier.TierUtils;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NextTierButton extends Button {

    private final Profile profile;

    public NextTierButton(Profile profile) {
        this.profile = profile;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        int currentTier = profile.getTier();
        int nextTier = currentTier + 1;
        
        List<String> lore = new ArrayList<>();
        
        TierCategory category = TierCategory.getCategoryByLevel(nextTier);
        TierCategory currentCategory = TierCategory.getCategoryByLevel(currentTier);

        lore.add(category.getColor() + category.getName() + " " + nextTier);
        lore.add("");
        lore.add("&bReward:");
        lore.add("&7- &b" + TierUtils.calculateTierReward(nextTier));
        if (!category.equals(currentCategory)) {
            lore.add("&7- " + category.getFormattedIcon() + " " + category.getName());
        }

        return new ItemBuilder(category.getMaterial())
                .name("&b&lNext Tier")
                .lore(lore)
                .data(3)
                .setHeadTexture(category.getTexture())
                .build();
    }
}
