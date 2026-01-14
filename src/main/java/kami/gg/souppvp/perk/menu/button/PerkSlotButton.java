package kami.gg.souppvp.perk.menu.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.perk.menu.adjust.AdjustPerksMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PerkSlotButton extends Button {

    private final int tier;

    public PerkSlotButton(int tier) {
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
                        "&fCurrent Perk: " + name,
                        "",
                        "&eClick here to select a Tier " + tier + " perk."
                )
                .build();
    }
    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.isLeftClick()) {
            playNeutral(player);
            new AdjustPerksMenu(tier).openMenu(player);
        }
    }

}
