package kami.gg.souppvp.events.impl.tnttag.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.listener.EventListener;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TNTTagListener extends EventListener {

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

    @Override
    protected Event getEvent(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Event activeEvent = profile.getActiveEvent();
        if (activeEvent instanceof TNTTagGame) return activeEvent;
        return null;
    }

    @Override
    protected boolean isPlaying(Player player, Event event) {
        if (!(event instanceof TNTTagGame game)) return false;
        return game.getEventPlayers().containsKey(player.getUniqueId());
    }

    @Override
    protected void handleDeath(Player player, Event event) {
        if (event instanceof TNTTagGame game) {
            game.handleDeath(player);
        }
    }

    @Override
    protected Location getSpectatorSpawn() {
        return SoupPvP.getInstance().getTntTagHandler().getSpectatorSpawn();
    }
}
