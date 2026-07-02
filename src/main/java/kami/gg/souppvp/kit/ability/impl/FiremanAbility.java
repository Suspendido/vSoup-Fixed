package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.TaskUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class FiremanAbility implements KitAbility {

    @Override
    public String getName() {
        return "Fireman";
    }

    @Override
    public String getDescription() {
        return "&fWater acts as Lava, Lava acts as Water";
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.LAVA_BUCKET).lore("Dont Display").build();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!hasAbility(player, profile, getName())) return;

        EntityDamageEvent.DamageCause cause = event.getCause();

        if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
            return;
        }

        if (cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            Location loc = player.getLocation();
            Material feet = loc.getBlock().getType();
            Material legs = loc.clone().add(0, 1, 0).getBlock().getType();

            boolean inLava = feet == Material.LAVA || feet == Material.STATIONARY_LAVA || legs == Material.LAVA || legs == Material.STATIONARY_LAVA;

            if (inLava) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile == null) return;
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!hasAbility(player, profile, getName())) return;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        Location loc = player.getLocation();
        Material feet  = loc.getBlock().getType();
        Material legs  = loc.clone().add(0, 1, 0).getBlock().getType();
        Material head  = loc.clone().add(0, 2, 0).getBlock().getType();

        boolean inLava  = feet == Material.LAVA  || feet == Material.STATIONARY_LAVA || legs == Material.LAVA  || legs == Material.STATIONARY_LAVA || head == Material.LAVA  || head == Material.STATIONARY_LAVA;
        boolean inWater = feet == Material.WATER || feet == Material.STATIONARY_WATER || legs == Material.WATER || legs == Material.STATIONARY_WATER || head == Material.WATER || head == Material.STATIONARY_WATER;

        if (inLava) {
            TaskUtil.runLater(() -> player.setFireTicks(0), 2L);
            return;
        }

        if (inWater) {
            if (player.getFireTicks() > 0) {
                TaskUtil.runLaterAsync(() -> player.setFireTicks(0), 2L);
            } else {
                TaskUtil.runLaterAsync(() -> player.setFireTicks(200), 2L);
            }
        }
    }
}
