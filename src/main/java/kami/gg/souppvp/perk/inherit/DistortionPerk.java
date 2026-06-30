package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class DistortionPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Distortion";
    }

    @Override
    public String getColor() {
        return "&8";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Attacking players has a 5% chance of", "&7blinding your enemies.");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.COAL);
    }

    @Override
    public int getCost() {
        return 750;
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player target)) return;

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(damager.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent()) return;
        if (!profile.getActivePerks().contains(getName())) return;

        if (new Random().nextInt(100) < 5) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
        }
    }

}
