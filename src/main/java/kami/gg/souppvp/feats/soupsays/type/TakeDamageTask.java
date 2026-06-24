package kami.gg.souppvp.feats.soupsays.type;

import kami.gg.souppvp.feats.soupsays.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class TakeDamageTask extends Tasks {

    @Override
    public String getTaskID() {
        return "TakeDamage";
    }

    @Override
    public String getTaskDisplayName() {
        return "Take Damage";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            this.addProgress((Player) event.getEntity());
        }
    }
}
