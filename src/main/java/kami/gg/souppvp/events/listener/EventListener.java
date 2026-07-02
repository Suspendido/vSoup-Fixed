package kami.gg.souppvp.events.listener;

import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.util.SpectatorUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class EventListener implements Listener {

    protected abstract Event getEvent(Player player);
    protected abstract boolean isPlaying(Player player, Event event);
    protected abstract void handleDeath(Player player, Event event);
    protected abstract Location getSpectatorSpawn();

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Event activeEvent = getEvent(player);
        if (activeEvent == null) return;

        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.VOID || cause == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
            player.setFireTicks(0);

            if (!isPlaying(player, activeEvent)) {
                player.teleport(getSpectatorSpawn());
                return;
            }

            SpectatorUtil.resetPlayer(player);
            player.teleport(getSpectatorSpawn());
            handleDeath(player, activeEvent);
            return;
        }

        if (!isPlaying(player, activeEvent)) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(0);
        player.setHealth(20.0);
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Event activeEvent = getEvent(player);
        if (activeEvent == null) return;
        if (!isPlaying(player, activeEvent)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Event activeEvent = getEvent(event.getPlayer());
        if (activeEvent != null) {
            activeEvent.handleLeave(event.getPlayer());
        }
    }
}
