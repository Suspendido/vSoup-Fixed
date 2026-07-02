package kami.gg.souppvp.listener.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.TaskUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    private final SoupPvP plugin = SoupPvP.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getClientHook().handleJoin(player);
        PlayerUtil.resetPlayer(player);
        plugin.getCreditsTimer().startTimer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getCreditsTimer().stopTimer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.SURVIVAL) return;
        if (!hasMovedBlock(event)) return;

        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;

        if (profile.getProfileState() != ProfileState.SPAWN) return;
        if (plugin.getSpawnHandler().getCuboid().contains(player)) return;

        player.sendMessage(CC.t("&7You no longer have spawn protection!"));
        profile.setProfileState(ProfileState.COMBAT);

        TaskUtil.runLater(() -> player.removeMetadata("noFall", plugin), 20L);

        if (profile.isJuggernaut()) return;

        Kit kit = plugin.getKitsHandler().getKitByName(profile.getCurrentKit());
        if (kit != null) {
            kit.equipKit(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!player.hasMetadata("noFall")) return;

        event.setCancelled(true);
        player.removeMetadata("noFall", plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpongeLaunch(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (!hasMovedBlock(event)) return;
        if (player.hasMetadata("jammed")) return;

        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.SPONGE) return;

        TaskUtil.runLater(() -> {
            player.setVelocity(player.getVelocity().setY(2.5));
            PlayerUtil.playSound(player, Sound.CHICKEN_EGG_POP, 1.0);
        }, 2L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDenyMovement(PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (!hasMovedBlock(event)) return;

        if (event.getPlayer().hasMetadata("denyMovement")) {
            event.setCancelled(true);
        }
    }

    private boolean hasMovedBlock(PlayerMoveEvent event) {
        return event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    }
}
