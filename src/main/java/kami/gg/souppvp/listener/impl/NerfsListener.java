package kami.gg.souppvp.listener.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author hieu
 * @date 11/6/2023
 */
public class NerfsListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        if (e.getCause() == EntityDamageEvent.DamageCause.MAGIC) {
            int level = 0;

            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                if (!activePotionEffect.getType().equals(PotionEffectType.HARM)) continue;
                level = activePotionEffect.getAmplifier() + 1;
                break;
            }

            switch (level) {
                case 1:
                    e.setDamage(e.getDamage() * 0.45);
                    break;

                case 2:
                    e.setDamage(e.getDamage() * 0.40);
                    break;

                default:
                    e.setDamage(e.getDamage() * 0.30);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;

        if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            int level = 0;

            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                if (!activePotionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) continue;
                level = activePotionEffect.getAmplifier() + 1;
                break;
            }

            switch (level) {
                case 1:
                    e.setDamage(e.getDamage() * 0.45);
                    break;

                case 2:
                    e.setDamage(e.getDamage() * 0.40);
                    break;

                default:
                    e.setDamage(e.getDamage() * 0.30);
            }
        }
    }

}