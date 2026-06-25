package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class FiremanAbility implements KitAbility {

    @Override
    public String getName() {
        return "Fireman";
    }

    @Override
    public String getDescription() {
        return "&fImmune to fire and lava damage";
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.LAVA_BUCKET)
                .name("&c&lFire Immunity")
                .lore(
                        "&7You are immune to:",
                        "&7- Fire damage",
                        "&7- Lava damage"
                )
                .build();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;

        EntityDamageEvent.DamageCause cause = event.getCause();

        if (cause != EntityDamageEvent.DamageCause.FIRE && 
            cause != EntityDamageEvent.DamageCause.LAVA && 
            cause != EntityDamageEvent.DamageCause.FIRE_TICK) return;

        event.setCancelled(true);
    }
}
