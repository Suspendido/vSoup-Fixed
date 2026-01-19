package kami.gg.souppvp.tier.button;

import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.Tiers;
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
        Tiers current = profile.getTier();
        Tiers next = current.getNext();

        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add("&b┃ &fCurrent Tier: &b" + current.getDisplay() + "✫");
        lore.add("&b┃ &fExperiences: &b" + profile.getExperiences());
        lore.add("");

        if (next != null) {
            lore.add("&b┃ &fNext Tier: &b" + next.getDisplay() + "✫");
            lore.add("&b┃ &fRequired XP: &b" + next.getRequiredExperiences());
            lore.add("");
            lore.add("&b┃ &fProgress:");
            lore.add("&b┃ &b" + Math.min(profile.getExperiences(), next.getRequiredExperiences()) + "&7/&b" + next.getRequiredExperiences());
        } else {
            lore.add("&a&lMAX TIER REACHED");
        }

        return new ItemBuilder(Material.EXP_BOTTLE)
                .name("&bTier Progress")
                .lore(lore)
                .build();
    }
}
