package kami.gg.souppvp.perk.inherit.tier3;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CanaPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Cana";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Water acts as lava, and lava acts as water"));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.WATER_BUCKET);
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
    public void EntityDamageEvent(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
            if (profile.isInEvent()) return;
            Perk canaPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Cana");
            if (event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK){
                if (SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(2)) == canaPerk){
                    event.setCancelled(true);
                }
            }
        }
    }

}
