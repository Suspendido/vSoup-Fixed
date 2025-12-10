package kami.gg.souppvp.nametag.task;

import kami.gg.souppvp.SoupPvP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("ALL")
public class NametagTask implements Runnable {

    @Override
    public void run() {
        try {
            Set<Player> onlinePlayers = new HashSet<>(Bukkit.getOnlinePlayers());

            for (Player viewer : onlinePlayers) {
                // Handle self-update (viewer seeing their own nametag)
                SoupPvP.getInstance().getNametagManager().handleUpdate(viewer, viewer);

                // Handle hidden players (staff mode) - these players need updates from staff
                for (Player staff : viewer.spigot().getHiddenPlayers()) {
                    if (staff == viewer) continue;
                    SoupPvP.getInstance().getNametagManager().handleUpdate(staff, viewer);
                }

                // Handle all other players that the viewer can see
                for (Player target : onlinePlayers) {
                    if (viewer == target) continue;
                    if (viewer.spigot().getHiddenPlayers().contains(target)) continue;
                    SoupPvP.getInstance().getNametagManager().handleUpdate(viewer, target);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}