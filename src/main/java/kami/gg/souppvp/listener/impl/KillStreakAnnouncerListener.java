package kami.gg.souppvp.listener.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillStreakAnnouncerListener implements Listener {

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getKiller().getUniqueId());
        Perk activePerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(2));
        Perk incognito = SoupPvP.getInstance().getPerksHandler().getPerkByName("Incognito");

        if (activePerk == incognito) return;

        if (profile.getCurrentKillstreak() % 5 == 0 && profile.getCurrentKillstreak() > 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile playerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
                if (playerProfile.getEnableKillstreakMessages()) {
                    player.sendMessage(CC.t("&a" + profile.getUsername() + "&e is on a &a" + profile.getCurrentKillstreak() + "&e killstreak!"));
                }
            }
        }
    }
}
