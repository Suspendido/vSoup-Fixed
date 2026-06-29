package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CactusAbility implements KitAbility {

    private static final Random RANDOM = new Random();

    @Override
    public String getName() {
        return "Cactus";
    }

    @Override
    public String getDescription() {
        return "&f25% chance to reflect 25% of damage back to attacker";
    }

    @Override
    public String getColor() {
        return "&2";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.CACTUS).lore("Dont Display").build();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player target)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!hasAbility(target, profile, getName())) return;

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(target)) {
            event.setCancelled(true);
            return;
        }

        if (RANDOM.nextDouble() <= 0.25) {
            double reflected = event.getDamage() * 0.25;
            damager.damage(reflected);
        }
    }
}
