package kami.gg.souppvp.perk.inherit.tier3;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CreditorPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Creditor";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Gain an additional 5 credits for"));
        lore.add(CC.t("&7every kill, but every death will"));
        lore.add(CC.t("&7result in 10 credits lost."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.EMERALD);
    }

    @Override
    public int getCost() {
        return 1250;
    }

    @Override
    public int getTier() {
        return 3;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        Profile killedProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
        if (killedProfile.isInEvent()) return;
        Perk creditorPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Creditor");
        if (SoupPvP.getInstance().getPerksHandler().getPerkByName(killedProfile.getActivePerks().get(2)) == creditorPerk){
            killedProfile.setCredits(killedProfile.getCredits() - 10);
        }
        if (event.getEntity().getKiller() != null){
            Profile killerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
            if (killerProfile.isInEvent()) return;
            if (SoupPvP.getInstance().getPerksHandler().getPerkByName(killerProfile.getActivePerks().get(2)) == creditorPerk){
                killerProfile.setCredits(killerProfile.getCredits() + 5);
            }
        }
    }

}
