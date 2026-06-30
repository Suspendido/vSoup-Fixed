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

import java.util.List;

public class FireFighterPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Fire Fighter";
    }

    @Override
    public String getColor() {
        return "&e";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Take 5% less damage, and deal", "&710% more damage while on fire.");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.FLINT_AND_STEEL);
    }

    @Override
    public int getCost() {
        return 1500;
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!(e.getDamager() instanceof Player damager)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        Profile damagedProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Profile damagerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(damager.getUniqueId());

        if (damagedProfile == null || damagerProfile == null) return;
        if (damagerProfile.isInEvent() || damagedProfile.isInEvent()) return;

        if (player.getFireTicks() > 0 && damagedProfile.getActivePerks().contains(getName())) {
            e.setDamage(e.getDamage() * 0.95);
        }

        if (damager.getFireTicks() > 0 && damagerProfile.getActivePerks().contains(getName())) {
            e.setDamage(e.getDamage() * 1.1);
        }
    }
}
