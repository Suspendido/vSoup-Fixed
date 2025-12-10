package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class BountyListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        if (event.getEntity().getKiller() != null){
            Profile killerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
            Profile victimProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
            if (!(event.getEntity().equals(event.getEntity().getKiller()))){
                if (victimProfile.getBounty() > 0){
                    Bukkit.getServer().broadcastMessage(CC.translate("&a" + event.getEntity().getKiller().getName() + "&e has claimed the &a" + victimProfile.getBounty() + "&e credit bounty from &a" + event.getEntity().getName() + "&e."));
                    killerProfile.setCredits(killerProfile.getCredits() + victimProfile.getBounty());
                    victimProfile.setBounty(0);
                }
            }
        }
    }

}
