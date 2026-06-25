package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
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
    private static final String LASER_NAME = "Laser Beam";
    private static final int LASER_COOLDOWN = 45;
    private static final int LASER_TICKS = 30;
    private static final double LASER_CHANCE_RED = 0.5;

    private final ItemStack laserItem = new ItemBuilder(Material.REDSTONE).name("&cLaser Beam").build();

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
        return laserItem.clone();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        ItemStack item = event.getItem();

        if (!isRightClick(event.getAction())) return;
        if (item == null || !item.isSimilar(laserItem)) return;

        event.setCancelled(true);
        player.updateInventory();

        if (profile.getProfileState() == ProfileState.SPAWN && SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage("&cYou can't use this while in spawn!");
            return;
        }

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), LASER_NAME, true)) {
            long remaining = SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), LASER_NAME, true);
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(remaining, true) + "&c."));
            return;
        }

        // Apply cooldown
        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(player.getUniqueId(), new Timer(LASER_NAME, TimeUnit.SECONDS.toMillis(LASER_COOLDOWN)), true);
        XPBarTimer.runXpBar(player, LASER_COOLDOWN);
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
                if (ticks++ >= LASER_TICKS || !player.isOnline()) {
                    cancel();
                    return;
                }

                boolean red = RANDOM.nextDouble() <= LASER_CHANCE_RED;
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
