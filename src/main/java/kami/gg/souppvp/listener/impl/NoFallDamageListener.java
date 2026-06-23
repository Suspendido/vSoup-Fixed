package kami.gg.souppvp.listener.impl;

import kami.gg.souppvp.SoupPvP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class NoFallDamageListener implements Listener {

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;

            if (SoupPvP.getInstance().getNoFallDamageHandler().getNoFallDamage().contains(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
                SoupPvP.getInstance().getNoFallDamageHandler().remove(event.getEntity().getUniqueId());
            }
        }
    }
}
