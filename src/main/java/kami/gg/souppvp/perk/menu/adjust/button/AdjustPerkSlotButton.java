package kami.gg.souppvp.perk.menu.adjust.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdjustPerkSlotButton extends Button {

    private final int tier;

    public AdjustPerkSlotButton(int tier) {
        this.tier = tier;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Perk currentPerk = null;

        if (profile.getActivePerks().size() >= tier) {
            String perkName = profile.getActivePerks().get(tier - 1);
            if (perkName != null) {
                currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(perkName);
            }
        }

        String name = currentPerk != null ? "&a" + currentPerk.getName() : "&6None";
        ItemStack icon = currentPerk != null && currentPerk.getIcon() != null ? currentPerk.getIcon() : new ItemStack(Material.BARRIER);
        return new ItemBuilder(icon)
                .name(name)
                .lore(
                        "&7Your active Tier " + tier + " perk.",
                        "",
                        "&fCurrent Perk: &a" + name,
                        "",
                        "&eAdjust your Tier " + tier + " perk below."
                ).build();
    }

}
