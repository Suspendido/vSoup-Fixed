package kami.gg.souppvp.tier.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.Tiers;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TierButton extends Button {

    private final Tiers tier;

    public TierButton(Tiers tier){
        this.tier = tier;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(CC.translate("&fDisplay: &b" + tier.getDisplay() + "✫"));
        lore.add(CC.translate("&fRequired Experiences: &b" + tier.getRequiredExperiences()));
        lore.add("");
        lore.add(CC.translate("&fYour Progress:"));
        if (profile.getExperiences() >= tier.getRequiredExperiences()){
            lore.add(CC.translate("&7• &fExperiences: &b" + tier.getRequiredExperiences() + "&f/&b" + tier.getRequiredExperiences()));
        } else {
            lore.add(CC.translate("&7• &fExperiences: &b" + profile.getExperiences() + "&f/&b" + tier.getRequiredExperiences()));
        }
        lore.add("");
        Tiers profileTier = profile.getTier();
        if (profileTier == tier){
            lore.add(CC.translate("&a&lYour Current Tier"));
        }
        return new ItemBuilder(Material.NAME_TAG).name(CC.translate("&b" + tier.getDisplay())).lore(lore).build();
    }
}
