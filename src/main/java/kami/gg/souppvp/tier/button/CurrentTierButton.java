package kami.gg.souppvp.tier.button;

import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CurrentTierButton extends Button {

    private final Profile profile;

    public CurrentTierButton(Profile profile) {
        this.profile = profile;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        int currentTier = profile.getTier();
        TierCategory category = TierCategory.getCategoryByLevel(currentTier);

        List<String> lore = new ArrayList<>();

        lore.add(category.getColor() + category.getName() + " " + currentTier);

        return new ItemBuilder(category.getMaterial())
                .name(category.getColor() + "&lCurrent Tier")
                .lore(lore)
                .setHeadTexture(category.getTexture())
                .data(3)
                .build();
    }
}
