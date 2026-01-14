package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SpawnListeners implements Listener {

    private final SoupPvP plugin = SoupPvP.getInstance();

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.getProfileState() == ProfileState.SPAWN || plugin.getSpawnHandler().getCuboid().contains(player)) {
            event.setCancelled(true);
        }
    }
}