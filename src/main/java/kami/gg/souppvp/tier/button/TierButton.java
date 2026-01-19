package kami.gg.souppvp.tier.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.Tiers;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TierButton extends Button {

    private final Tiers tier;

    public TierButton(Tiers tier) {
        this.tier = tier;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Tiers profileTier = profile.getTier();
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add("&b┃ &fDisplay: &b" + tier.getDisplay() + "✫");
        lore.add("&b┃ &fRequired Experiences: &b" + tier.getRequiredExperiences());
        lore.add("");
        lore.add("&b┃ &fYour Progress:");
        lore.add(profile.getExperiences() >= tier.getRequiredExperiences()
                ? "&b┃ &fExperiences: &b" + tier.getRequiredExperiences() + "&f/&b" + tier.getRequiredExperiences()
                : "&b┃ &fExperiences: &b" + profile.getExperiences() + "&f/&b" + tier.getRequiredExperiences()
        );
        lore.add("");

        if (profileTier == tier) {
            lore.add("&a&lYour Current Tier");
        } else if (profileTier.getTierLevel() > tier.getTierLevel()) {
            lore.add("&a✔ Completed");
        }

        ItemBuilder builder = new ItemBuilder(Material.NAME_TAG)
                .name("&b" + tier.getDisplay())
                .lore(lore);

        if (profileTier.getTierLevel() > tier.getTierLevel()) {
            builder.enchantment(Enchantment.DURABILITY, 1).hideAttributes();
        }

        return builder.build();
    }

}
