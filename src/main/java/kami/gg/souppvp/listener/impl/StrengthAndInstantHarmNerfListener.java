package kami.gg.souppvp.listener.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * @author hieu
 * @date 11/6/2023
 */
public class StrengthAndInstantHarmNerfListener implements Listener {

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            if (event.getCause() == EntityDamageEvent.DamageCause.MAGIC){
                event.setDamage(event.getDamage() / 1.25);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                event.setDamage(event.getDamage() * 0.23);
            }
        }
    }

}