package kami.gg.souppvp.listener.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.*;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GeneralListeners implements Listener {

    private static final Random RANDOM = new Random();
    private final SoupPvP plugin = SoupPvP.getInstance();

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        int random = ThreadLocalRandom.current().nextInt(100 + 1);
        if (random < 50) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);

        Profile playerProfile = plugin.getProfilesHandler().getProfileByUUID(victim.getUniqueId());

        if (playerProfile != null && playerProfile.isInEvent()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> victim.spigot().respawn(), 2L);
            return;
        }

        // Conartist perk check
        if (playerProfile != null && !playerProfile.getActivePerks().isEmpty() && playerProfile.getActivePerks().size() > 2) {
            Perk profilePerk = plugin.getPerksHandler().getPerkByName(playerProfile.getActivePerks().get(2));
            Perk conartistPerk = plugin.getPerksHandler().getPerkByName("Conartist");

            if (profilePerk != null && profilePerk.equals(conartistPerk) && RANDOM.nextInt(101) <= 50) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> victim.spigot().respawn(), 2L);
                return;
            }
        }

        // Drop mushroom soup
        Location deathLocation = victim.getLocation();
        World world = victim.getWorld();
        ItemStack mushroom = new ItemStack(Material.MUSHROOM_SOUP);

        for (int i = 0; i < 9; i++) {
            world.dropItemNaturally(deathLocation, mushroom);
        }

        // Remove dropped items after 3 seconds
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Entity entity : world.getNearbyEntities(deathLocation, 5, 5, 5)) {
                if (entity.getType() == EntityType.DROPPED_ITEM) {
                    entity.remove();
                }
            }
        }, 60L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> victim.spigot().respawn(), 2L);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location spawn = Bukkit.getWorlds().getFirst().getSpawnLocation().add(0.5, 0, 0.5);

        player.teleport(spawn);
        PlayerUtil.resetPlayer(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata("build")) {
            event.setCancelled(false);
            event.setUseItemInHand(Event.Result.DEFAULT);
            return;
        }

        if (plugin.getSpawnHandler().getCuboid().contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().hasMetadata("build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().hasMetadata("build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.SURVIVAL) return;

        ItemStack item = event.getItemDrop().getItemStack();
        Material type = item.getType();
        String typeName = type.name().toLowerCase();

        // Prevent dropping weapons
        if (typeName.contains("sword") || typeName.contains("axe") || type == Material.BOW) {
            player.sendMessage(CC.t("&cYou can't drop your attacking weapon."));
            event.setCancelled(true);
            return;
        }

        // Prevent dropping armor
        if (typeName.contains("helmet") || typeName.contains("chestplate") || typeName.contains("leggings") || typeName.contains("boots")) {
            player.sendMessage(CC.t("&cYou can't drop your armor."));
            event.setCancelled(true);
            return;
        }

        // Remove bowls instantly, other items after 5 seconds
        if (type == Material.BOWL) {
            event.getItemDrop().remove();
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> event.getItemDrop().remove(), 100L);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

        Profile profile = plugin.getProfilesHandler().getProfileByUUID(event.getPlayer().getUniqueId());

        if (profile != null && profile.getProfileState() == ProfileState.SPAWN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        World world = Bukkit.getWorld("world");
        if (world == null) return;

        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(6000);
        world.setStorm(false);
        world.setWeatherDuration(0);
        world.setAnimalSpawnLimit(0);
        world.setAmbientSpawnLimit(0);
        world.setMonsterSpawnLimit(0);
        world.setWaterAnimalSpawnLimit(0);
        world.setDifficulty(Difficulty.HARD);
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            String[] lines = event.getLines();
            for (int i = 0; i < lines.length; i++) {
                event.setLine(i, CC.t(lines[i]));
            }
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (event.getNewState().getType() == Material.FIRE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> event.getEntity().remove(), 150L);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        int id = event.getBlock().getTypeId();
        if (id == 8 || id == 9) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEnderPearlThrow(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        Player player = event.getPlayer();

        if (plugin.getSpawnHandler().getCuboid().contains(event.getTo())) {
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            player.sendMessage(CC.t("&cYou may not pearl into spawn."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() == event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;
        Player player = event.getPlayer();
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile == null || profile.getProfileState() != ProfileState.COMBAT) return;
        if (!plugin.getSpawnHandler().getCuboid().contains(player)) return;

        // Find nearest safe location
        Location current = player.getLocation();
        Location safe = null;
        double minDistance = Double.MAX_VALUE;

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location check = current.clone().add(x, 0, z);

                if (!plugin.getSpawnHandler().getCuboid().contains(check)) {
                    double distance = check.distanceSquared(current);
                    if (distance < minDistance) {
                        minDistance = distance;
                        safe = check;
                    }
                }
            }
        }

        if (safe != null) {
            player.teleport(safe.add(0.5, 0, 0.5));
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }
}