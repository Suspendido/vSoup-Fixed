package kami.gg.souppvp.perk.menu.adjust.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class AdjustResetPerkButton extends Button {

    private final int tier;

    public AdjustResetPerkButton(int tier) {
        this.tier = tier;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.REDSTONE)
                .name("&cReset Perk")
                .lore(
                        "&7Remove your selected Tier " + tier + " perk.",
                        "",
                        "&eClick here to &creset &eyour Tier " + tier + " perk."
                ).build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.isLeftClick()) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            Perk currentTierPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(tier-1));

            if (currentTierPerk == null) {
                playFail(player);
                sendMessage(player, "&cYou do not have an active perk!");
                return;
            }

            Perk hardlinePerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Hardline");

            if (SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(tier-1)) == hardlinePerk) {
                if (profile.getCurrentKillstreak() > 0) {
                    sendMessage(player, "&cYou lost your killstreak of " + profile.getCurrentKillstreak() + "!");
                }
                profile.setCurrentKillstreak(0);
            }

            profile.getActivePerks().set(tier-1, "None");
            sendMessage(player, "&aYou have removed your tier " + tier + " perk.");
            playNeutral(player);
        }
    }
}
