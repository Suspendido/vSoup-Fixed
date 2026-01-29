package kami.gg.souppvp.listener.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class PvPListeners implements Listener {

    private final SoupPvP plugin = SoupPvP.getInstance();

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());

        // Cancel spawn teleport if damaged
        if (profile.isTeleportingToSpawn()) {
            profile.removeSpawnTeleportation();
            player.sendMessage(CC.translate("&cYour spawn teleportation was cancelled because you were combat-tagged."));
        }

        // No tag if player is in spawn
        if (profile.getProfileState() == ProfileState.SPAWN) return;
        if (plugin.getSpawnHandler().getCuboid().contains(player)) return;

//        profile.addCombatTag();
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged) || !(event.getDamager() instanceof Player damager)) return;

        Profile damagedProfile = plugin.getProfilesHandler().getProfileByUUID(damaged.getUniqueId());
        Profile damagerProfile = plugin.getProfilesHandler().getProfileByUUID(damager.getUniqueId());

        // Prevent combat in spawn
        if (damagerProfile.getProfileState() == ProfileState.SPAWN || damagedProfile.getProfileState() == ProfileState.SPAWN) return;
        if (plugin.getSpawnHandler().getCuboid().contains(damager) || plugin.getSpawnHandler().getCuboid().contains(damaged)) return;

        damagerProfile.addCombatTag();
        damagedProfile.addCombatTag();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());

        Player killer = player.getKiller();
        boolean killedByPlayer = killer != null && killer != player;
        if (profile.isInEvent()) return;

        if (killedByPlayer) {
            Profile killerProfile = plugin.getProfilesHandler().getProfileByUUID(killer.getUniqueId());

            if (!killerProfile.isJuggernaut()) {

                // Stats update
                killerProfile.setKills(killerProfile.getKills() + 1);
                killerProfile.setCurrentKillstreak(killerProfile.getCurrentKillstreak() + 1);

                if (killerProfile.getCurrentKillstreak() > killerProfile.getHighestKillstreak()) {
                    killer.sendMessage(CC.translate("&aNew Highest Killstreak! &fYou're now at &a" + killerProfile.getCurrentKillstreak()));
                    killerProfile.setHighestKillstreak(killerProfile.getCurrentKillstreak());
                }

                int credits = ThreadLocalRandom.current().nextInt(1, 21);
                int xp = ThreadLocalRandom.current().nextInt(1, 5);
                boolean proKit = "Pro".equalsIgnoreCase(killerProfile.getCurrentKit());
                boolean easySoupDisabled = !killerProfile.getEnableEasySoup();

                if (proKit) credits *= 2;
                if (easySoupDisabled) credits *= 2;

                killerProfile.setCredits(killerProfile.getCredits() + credits);
                killerProfile.setExperiences(killerProfile.getExperiences() + 3);

                // Kill messages
                if (killerProfile.getEnableKillDeathMessages()) {
                    killer.sendMessage(CC.translate("&9You killed &a" + player.getName() + "&9 for &a" + credits + " &9credits and &a" + xp +" XP."));
                }

                if (profile.getEnableKillDeathMessages()) {
                    player.sendMessage(CC.translate("&cYou were killed by &a" + killer.getName()));
                }

                // Broadcasts
                for (Profile p : plugin.getProfilesHandler().getProfiles().values()) {
                    if (!p.getEnableKillDeathMessages()) continue;

                    Player online = Bukkit.getPlayer(p.getUuid());
                    if (online == null) continue;

                    if (profile.getCurrentKillstreak() >= 10) {
                        online.sendMessage(CC.translate("&e" + profile.getUsername() + " &adied with a &e" + profile.getCurrentKillstreak() + " &akillstreak!"));
                    }
                }
            }
        } else {
            // Death by environment
            for (Profile p : plugin.getProfilesHandler().getProfiles().values()) {
                if (!p.getEnableKillDeathMessages()) continue;

                Player online = Bukkit.getPlayer(p.getUuid());
                if (online == null) continue;

                if (p.equals(profile)) {
                    online.sendMessage(CC.translate("&cYou died."));
                }
            }
        }

        // Kit progress
        plugin.getKitProgressManager().handleDeath(profile);

        if (killedByPlayer) {
            Profile killerProfile = plugin.getProfilesHandler().getProfileByUUID(killer.getUniqueId());
            plugin.getKitProgressManager().handleKill(killerProfile);
        }

        // Reset stats
        profile.setCurrentKillstreak(0);
        profile.setDeaths(profile.getDeaths() + 1);
    }

    @EventHandler
    public void onSoupRefillSign(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Material type = event.getClickedBlock().getType();

        if (type != Material.SIGN && type != Material.SIGN_POST && type != Material.WALL_SIGN) return;

        Player player = event.getPlayer();
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());

        Sign sign = (Sign) event.getClickedBlock().getState();

        if (!sign.getLine(0).contains("Free") || !sign.getLine(1).contains("Soup")) return;

        if (profile.isJuggernaut()) {
            player.sendMessage(CC.translate("&cYou cannot refill soups while in Juggernaut."));
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 54, "Refill station");

        ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, soup.clone());

        player.openInventory(inv);
    }
}