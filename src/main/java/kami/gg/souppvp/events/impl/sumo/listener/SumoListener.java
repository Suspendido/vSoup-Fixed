package kami.gg.souppvp.events.impl.sumo.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.listener.EventListener;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.SpectatorUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class SumoListener extends EventListener {

    @EventHandler
    public void onPlayerMoveEventWater(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Event activeEvent = getEvent(player);
        if (!(activeEvent instanceof Sumo sumo)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        Material block = player.getLocation().getBlock().getType();
        if (block != Material.WATER && block != Material.STATIONARY_WATER) return;

        SpectatorUtil.resetPlayer(player);
        player.teleport(getSpectatorSpawn());
        sumo.handleDeath(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker = getAttacker(event);
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (attacker == null) return;

        Event damagedEvent = getEvent(damaged);
        Event attackerEvent = getEvent(attacker);
        if (!(damagedEvent instanceof Sumo sumo)) return;
        if (damagedEvent != attackerEvent) return;
        if (!sumo.isFighting() || !sumo.isFighting(damaged.getUniqueId()) || !sumo.isFighting(attacker.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @Override
    protected Event getEvent(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Event activeEvent = profile.getActiveEvent();
        if (activeEvent instanceof Sumo) return activeEvent;
        return null;
    }

    @Override
    protected boolean isPlaying(Player player, Event event) {
        if (!(event instanceof Sumo sumo)) return false;
        return sumo.isFighting(player.getUniqueId());
    }

    @Override
    protected void handleDeath(Player player, Event event) {
        if (event instanceof Sumo sumo) {
            sumo.handleDeath(player);
        }
    }

    @Override
    protected Location getSpectatorSpawn() {
        return SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn();
    }

    private Player getAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) return player;
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) return player;
        return null;
    }
}
