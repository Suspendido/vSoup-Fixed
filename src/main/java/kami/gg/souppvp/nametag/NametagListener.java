package kami.gg.souppvp.nametag;

import kami.gg.souppvp.SoupPvP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NametagListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST) // call first
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SoupPvP.getInstance().getNametagManager().getNametags().put(player.getUniqueId(), new Nametag(player));
    }
}