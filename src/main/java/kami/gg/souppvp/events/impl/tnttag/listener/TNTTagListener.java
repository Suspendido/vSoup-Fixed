package kami.gg.souppvp.events.impl.tnttag.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.SpectatorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TNTTagListener implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        TNTTagGame game = SoupPvP.getInstance().getTntTagHandler().getActiveGame();

        if (game == null) return;
        if (!game.getEventPlayers().containsKey(victim.getUniqueId()) || !game.getEventPlayers().containsKey(damager.getUniqueId())) return;

        if (game.getState() != EventState.RUNNING) {
            event.setCancelled(true);
            return;
        }

        if (!damager.getUniqueId().equals(game.getTntHolder())) return;

        game.setTntHolder(victim.getUniqueId());
        game.removeTNT(damager);
        game.applyTNT(victim);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Event activeEvent = profile.getActiveEvent();
        if (!(activeEvent instanceof TNTTagGame game)) return;
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.VOID || cause == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
            player.setFireTicks(0);

            if (!game.getEventPlayers().containsKey(player.getUniqueId())) {
                player.teleport(SoupPvP.getInstance().getTntTagHandler().getSpectatorSpawn());
                return;
            }

            SpectatorUtil.resetPlayer(player);
            player.teleport(SoupPvP.getInstance().getTntTagHandler().getSpectatorSpawn());
            game.handleDeath(player);
            return;
        }

        if (!game.getEventPlayers().containsKey(player.getUniqueId())) {
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
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Event activeEvent = profile.getActiveEvent();
        if (!(activeEvent instanceof TNTTagGame game)) return;
        if (!game.getEventPlayers().containsKey(player.getUniqueId())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getPlayer().getUniqueId());
        Event activeEvent = profile.getActiveEvent();
        if (activeEvent != null) {
            activeEvent.handleLeave(event.getPlayer());
        }
    }
}
