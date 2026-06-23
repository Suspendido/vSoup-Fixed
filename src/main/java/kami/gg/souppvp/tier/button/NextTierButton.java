package kami.gg.souppvp.tier.button;

import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.tier.Tiers;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
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
        Tiers currentTier = profile.getTier();
        Tiers nextTier = currentTier.getNext();
        
        List<String> lore = new ArrayList<>();
        
        if (nextTier == null) {
            lore.add("&7You have reached the");
            lore.add("&amaximum tier!");
            lore.add("");
            lore.add("&aCongratulations!");
            
            return new ItemBuilder(Material.DIAMOND_BLOCK)
                    .name("&aMaximum Tier")
                    .lore(lore)
                    .build();
        }
        
        TierCategory category = TierCategory.getCategoryByLevel(nextTier.getTierLevel());
        TierCategory currentCategory = TierCategory.getCategoryByLevel(currentTier.getTierLevel());

        lore.add(category.getColor() + category.getName() + " " + nextTier.getTierLevel());
        lore.add("");
        lore.add(category.getColor() + "Reward:");
        lore.add("&7- &b" + nextTier.getCreditsReward());
        if (!category.equals(currentCategory)) {
            lore.add("&7- " + category.getFormattedIcon() + " " + category.getName());
        }

        return new ItemBuilder(category.getMaterial())
                .name(category.getColor() + "&lNext Tier")
                .lore(lore)
                .data(3)
                .setHeadTexture(category.getTexture())
                .build();
    }
}
