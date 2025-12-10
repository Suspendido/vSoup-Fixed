package kami.gg.souppvp.perk.menu.adjust.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AdjustResetPerkButton extends Button {

    private final int tier;

    public AdjustResetPerkButton(int tier){
        this.tier = tier;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(CC.translate("&7Remove your selected Tier " + tier + " perk."));
        lore.add("");
        lore.add(CC.translate("&eClick here to &creset &eyour Tier " + tier + " perk."));
        return new ItemBuilder(Material.STAINED_GLASS_PANE).name(CC.translate("&cReset Perk")).lore(lore).durability(14).build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.isLeftClick()){
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            Perk currentTierPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(tier-1));
            if (currentTierPerk == null) {
                playFail(player);
                return;
            }
            Perk hardlinePerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Hardline");
            if (SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(tier-1)) == hardlinePerk){
                profile.setCurrentKillstreak(0);
            }
            profile.getActivePerks().set(tier-1, "None");
            playNeutral(player);
        }
    }
}
