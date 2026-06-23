package kami.gg.souppvp.listener.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpawnTeleporatationListener implements Listener {

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (!player.getGameMode().equals(GameMode.CREATIVE)){
            if (profile.isTeleportingToSpawn()){
                if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() == event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;
                profile.removeSpawnTeleportation();
                Bukkit.getPlayer(player.getUniqueId()).sendMessage(CC.t("&cYour spawn teleportation has been cancelled because you have moved."));
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player damager) {
            Profile victimProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(victim.getUniqueId());
            Profile damagerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(damager.getUniqueId());

            if (victimProfile.isTeleportingToSpawn()) {
                victimProfile.removeSpawnTeleportation();
                Bukkit.getPlayer(victim.getUniqueId()).sendMessage(CC.t("&cYour spawn teleportation you have been combat-tagged."));
            }

            if (damagerProfile.isTeleportingToSpawn()) {
                damagerProfile.removeSpawnTeleportation();
                Bukkit.getPlayer(damager.getUniqueId()).sendMessage(CC.t("&cYour spawn teleportation you have been combat-tagged."));
            }
        }
    }

}
