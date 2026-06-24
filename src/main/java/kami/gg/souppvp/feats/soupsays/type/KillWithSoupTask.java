package kami.gg.souppvp.feats.soupsays.type;

import kami.gg.souppvp.feats.soupsays.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class KillWithSoupTask extends Tasks {

    @Override
    public String getTaskID() {
        return "KillWithSoup";
    }

    @Override
    public String getTaskDisplayName() {
        return "Kill a Player with Soup";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer == null) return;

        if (killer.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
            this.addProgress(killer);
        }
    }
}
