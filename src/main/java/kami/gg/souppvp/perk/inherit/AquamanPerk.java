package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AquamanPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Aquaman";
    }

    @Override
    public String getColor() {
        return "&b";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7While in water, you deal 10%", "&7more melee damage");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.RAW_FISH).durability(3).build();
    }

    @Override
    public int getCost() {
        return 750;
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent()) return;
        if (!profile.getActivePerks().contains(getName())) return;

        Location loc = player.getLocation();
        Material feet = loc.getBlock().getType();
        Material legs = loc.clone().add(0, 1, 0).getBlock().getType();

        boolean inWater = feet == Material.WATER || feet == Material.STATIONARY_WATER || legs == Material.WATER || legs == Material.STATIONARY_WATER;

        if (inWater) {
            event.setDamage(event.getDamage() * 1.1);
        }
    }
}
