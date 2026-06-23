package kami.gg.souppvp.perk.inherit.tier3;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FireFighterPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Fire Fighter";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Take 5% less damage, and deal"));
        lore.add(CC.t("&710% more damage while on fire."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.FLINT_AND_STEEL);
    }

    @Override
    public int getCost() {
        return 1500;
    }

    @Override
    public int getTier() {
        return 3;
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            Profile damagedProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
            Profile damagerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getDamager().getUniqueId());
            Perk fireFighterPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Fire Fighter");
            if (event.getEntity().getFireTicks() > 0){
                if (SoupPvP.getInstance().getPerksHandler().getPerkByName(damagedProfile.getActivePerks().get(2)) == fireFighterPerk){
                    event.setDamage(event.getDamage() * 0.95);
                    //Takes 5% less damage
                }
            }
            if (event.getDamager().getFireTicks() > 0){
                if (SoupPvP.getInstance().getPerksHandler().getPerkByName(damagerProfile.getActivePerks().get(2)) == fireFighterPerk){
                    event.setDamage(event.getDamage() * 1.1);
                    //Deals 10% more damage
                }
            }
        }
    }

}
