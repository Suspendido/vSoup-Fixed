package kami.gg.souppvp.profile;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.changelog.ChangeLog;
import kami.gg.souppvp.changelog.menu.ChangeLogViewMenu;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileListeners implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getUniqueId());

            if (profile == null || !profile.getLoaded()) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(CC.t("&cFailed to load your profile."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(CC.t("&cFailed to load your profile."));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setUsername(player.getName());

        // Check if current kit is available (exists and enabled)
        if (!SoupPvP.getInstance().getKitsHandler().isKitAvailable(profile.getCurrentKit())) {
            Kit fallbackKit = SoupPvP.getInstance().getKitsHandler().getKitByName("Default");
            if (fallbackKit != null) {
                // Refund credits if player had purchased the deleted kit
                if (profile.getUnlockedKits().contains(profile.getCurrentKit())) {
                    Kit deletedKit = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());
                    if (deletedKit != null) {
                        profile.getUnlockedKits().remove(profile.getCurrentKit());
                        profile.setCredits(profile.getCredits() + deletedKit.getPrice());
                        player.sendMessage(CC.t("&eThe kit &r" + deletedKit.getRarityType().getColor() + deletedKit.getName() + " &ehas been removed from the server."));
                        player.sendMessage(CC.t("&aYou have been refunded &6" + deletedKit.getPrice() + " &acredits."));
                    }
                }
                
                profile.setCurrentKit(fallbackKit.getName());
                profile.setPreviousKit(fallbackKit.getName());
                player.sendMessage(CC.t("&cYour previous kit is no longer available. You have been given the Default kit."));
            }
        }

        // Check if previous kit is available
        if (!SoupPvP.getInstance().getKitsHandler().isKitAvailable(profile.getPreviousKit())) {
            Kit fallbackKit = SoupPvP.getInstance().getKitsHandler().getKitByName("Default");
            if (fallbackKit != null) {
                // Refund credits if player had purchased the deleted kit
                if (profile.getUnlockedKits().contains(profile.getPreviousKit())) {
                    Kit deletedKit = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getPreviousKit());
                    if (deletedKit != null) {
                        profile.getUnlockedKits().remove(profile.getPreviousKit());
                        profile.setCredits(profile.getCredits() + deletedKit.getPrice());
                        player.sendMessage(CC.t("&eThe kit &r" + deletedKit.getRarityType().getColor() + deletedKit.getName() + " &ehas been removed from the server."));
                        player.sendMessage(CC.t("&aYou have been refunded &6" + deletedKit.getPrice() + " &acredits."));
                    }
                }
                
                profile.setPreviousKit(fallbackKit.getName());
            }
        }

        // Check for unread changelogs
        if (SoupPvP.getInstance().getChangeLogHandler() != null) {
            java.util.List<ChangeLog> unreadChangeLogs = SoupPvP.getInstance().getChangeLogHandler().getUnreadChangeLogs(player.getUniqueId());
            if (!unreadChangeLogs.isEmpty()) {
                player.sendMessage(CC.t("&e&lNEW CHANGELOG! &7There are &e" + unreadChangeLogs.size() + " &7new changelog" + (unreadChangeLogs.size() > 1 ? "s" : "") + " available."));
                player.sendMessage(CC.t("&7Opening changelog menu..."));
                new ChangeLogViewMenu(player).open();
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isCombatTagged()) {
            profile.setCurrentKillstreak(0);
            profile.setDeaths(profile.getDeaths() + 1);
        }
        profile.saveProfile();
        SoupPvP.getInstance().getTimerManager().getTimer("Combat").removeTimer(player);
        SoupPvP.getInstance().getNoFallDamageHandler().getNoFallDamage().remove(player.getUniqueId());
    }
}
