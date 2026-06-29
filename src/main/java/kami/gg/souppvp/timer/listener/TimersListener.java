package kami.gg.souppvp.timer.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.timer.Timer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author hieu
 * @date 24/06/2023
 */

public class TimersListener implements Listener {

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        for (Timer timer : SoupPvP.getInstance().getTimerManager().getPlayerTimers().values()) {
            timer.removeTimer(player);
        }
    }

}
