package kami.gg.souppvp.feats.tablist;

import kami.gg.souppvp.SoupPvP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class TablistListener implements Listener {
   
   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      if (!player.isOnline()) return;

      SoupPvP.getInstance().getTablistManager().updatePlayerTablist(player);
      SoupPvP.getInstance().getTablistManager().updateAllTablists();
   }
   
   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerQuit(PlayerQuitEvent event) {
      event.setQuitMessage(null);
      SoupPvP.getInstance().getTablistManager().updateAllTablists();
   }
}
