package kami.gg.souppvp.perk.inherit.tier2;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.TaskUtil;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class InfernoPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Inferno";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7After being on fire for 10 seconds"));
        lore.add(CC.t("&7You will be extinguished."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.FIREBALL);
    }

    @Override
    public int getCost() {
        return 850;
    }

    @Override
    public int getTier() {
        return 2;
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player)) return;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1));
        Perk infernoPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Inferno");
        if (currentPerk == infernoPerk){
            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.FIRE) {
                TaskUtil.runLater(() -> {
                    if (event.getEntity().getFireTicks() > 0) {
                        event.getEntity().setFireTicks(0);
                        event.getEntity().getLocation().getWorld().playSound(event.getEntity().getLocation(), Sound.WITHER_SHOOT, 1F, 1F);
                        event.getEntity().getWorld().spigot().playEffect(event.getEntity().getLocation().add(new Vector(0, 1.5, 0)), Effect.LARGE_SMOKE, 26, 0, 0.1F, 0.5F, 0.1F, 0.2F, 50, 50);
                    }
                }, 20 * 10);
            }
        }
    }

}
