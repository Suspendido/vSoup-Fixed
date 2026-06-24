package kami.gg.souppvp.feats.soupsays.type;

import kami.gg.souppvp.feats.soupsays.Tasks;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class HealTask extends Tasks {

    @Override
    public String getTaskID() {
        return "Heal";
    }

    @Override
    public String getTaskDisplayName() {
        return "Heal with Soup";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.MUSHROOM_SOUP) {
            this.addProgress(event.getPlayer());
        }
    }
}
