package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class StuntDevilPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Stunt Devil";
    }

    @Override
    public String getColor() {
        return "&5";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7If you fall from 25 blocks or more", "&7there is a 50% chance you will not", "&7take any fall damage.");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.ENDER_PEARL);
    }

    @Override
    public int getCost() {
        return 500;
    }

    @EventHandler
    public void onEntityDamageEventFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getCause() == EntityDamageEvent.DamageCause.FALL)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent()) return;
        if (!profile.getActivePerks().contains(getName())) return;

        if (new Random().nextDouble() <= 0.5) {
            event.setCancelled(true);
        }
    }

}
