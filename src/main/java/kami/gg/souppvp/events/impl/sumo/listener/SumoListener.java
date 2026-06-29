package kami.gg.souppvp.events.impl.sumo.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.SpectatorUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SumoListener implements Listener {

    @EventHandler
    public void onPlayerMoveEventWater(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        Event activeEvent = profile.getActiveEvent();
        if (!(activeEvent instanceof Sumo sumo)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        Material block = player.getLocation().getBlock().getType();
        if (block != Material.WATER && block != Material.STATIONARY_WATER) return;

        SpectatorUtil.resetPlayer(player);
        player.teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
        sumo.handleDeath(player);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Event activeEvent = profile.getActiveEvent();
        if (!(activeEvent instanceof Sumo sumo)) return;
        if (!sumo.isFighting(player.getUniqueId())) event.setCancelled(true);
    }


    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Event activeEvent = profile.getActiveEvent();
        if (!(activeEvent instanceof Sumo sumo)) return;
        EntityDamageEvent.DamageCause cause = event.getCause();

        if (cause == EntityDamageEvent.DamageCause.VOID || cause == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
            player.setFireTicks(0);

            if (!sumo.isFighting(player.getUniqueId())) {
                player.teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
                return;
            }

            SpectatorUtil.resetPlayer(player);
            player.teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
            sumo.handleDeath(player);
            return;
        }

        if (!sumo.isFighting(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(0);
        player.setHealth(20.0);
        player.updateInventory();
    }

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker = getAttacker(event);
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (attacker == null) return;

        Profile damagedProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(damaged.getUniqueId());
        Profile attackerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(attacker.getUniqueId());
        Event damagedEvent = damagedProfile.getActiveEvent();
        Event attackerEvent = attackerProfile.getActiveEvent();
        if (!(damagedEvent instanceof Sumo sumo)) return;
        if (damagedEvent != attackerEvent) return;
        if (!sumo.isFighting() || !sumo.isFighting(damaged.getUniqueId()) || !sumo.isFighting(attacker.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getPlayer().getUniqueId());
        Event activeEvent = profile.getActiveEvent();
        if (activeEvent != null) {
            activeEvent.handleLeave(event.getPlayer());
        }
    }

    private Player getAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) return player;
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) return player;
        return null;
    }

}
