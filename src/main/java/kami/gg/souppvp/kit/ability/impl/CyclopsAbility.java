package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.util.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.*;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CyclopsAbility implements KitAbility {

    private static final Random RANDOM = new Random();

    private final Timer laserTimer;

    public CyclopsAbility() {
        this.laserTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(45));
        SoupPvP.getInstance().getTimerManager().registerTimer(laserTimer);
    }

    @Override
    public String getName() {
        return "Cyclops";
    }

    @Override
    public String getDescription() {
        return "&fShoot laser beams that penetrate blocks";
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.REDSTONE).name("&cLaser Beam").build();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        ItemStack item = event.getItem();

        if (!isRightClick(event.getAction())) return;
        if (item == null || !AbilityItemComparator.isSameAbilityItem(item, getItem())) return;

        // Check if player has Cyclops ability
        if (!hasAbility(player, profile, getName())) return;

        event.setCancelled(true);
        player.updateInventory();

        if (profile.getProfileState() == ProfileState.SPAWN && SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage("&cYou can't use this while in spawn!");
            return;
        }

        if (laserTimer.hasTimer(player)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(laserTimer.getRemaining(player), true) + "&c."));
            return;
        }

        // Apply cooldown
        laserTimer.applyTimer(player);
        XPBarTimer.runXpBar(player, 45);
        PlayerUtil.playSound(player, Sound.GHAST_SCREAM, 1.0);
        player.getNearbyEntities(5, 5, 5)
                .stream()
                .filter(e -> e instanceof Player)
                .forEach(e -> PlayerUtil.playSound((Player) e, Sound.ZOMBIE_REMEDY, 1.0));

        // Start laser bursts
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ >= 30 || !player.isOnline()) {
                    cancel();
                    return;
                }

                boolean red = RANDOM.nextDouble() <= 0.5;
                spawnLaserBeam(player, red);
            }
        }.runTaskTimer(SoupPvP.getInstance(), 1L, 1L);
    }

    private void spawnLaserBeam(Player player, boolean red) {
        new BukkitRunnable() {
            final Vector direction = player.getEyeLocation().getDirection().normalize();
            final Location loc = player.getEyeLocation().clone();
            int steps = 0;

            @Override
            public void run() {
                if (!player.isOnline() || steps++ > 25) {
                    cancel();
                    return;
                }

                loc.add(direction.clone().multiply(0.7));

                // Spawn particle
                player.getWorld().spigot().playEffect(
                        loc,
                        Effect.COLOURED_DUST,
                        0,
                        0,
                        red ? 1.0f : 0.2f,
                        0.0f,
                        0.0f,
                        1.0f,
                        0,
                        16
                );

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 0.7, 0.7, 0.7)) {
                    if (!(entity instanceof Player target)) continue;
                    if (target == player) continue;

                    Profile p = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());

                    if (p.getProfileState() == ProfileState.SPAWN) continue;

                    target.damage(4.0, player);
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(SoupPvP.getInstance(), 0L, 1L);
    }

    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getEntity().hasMetadata("laser_beam")) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }
}
