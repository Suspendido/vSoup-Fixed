package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.TaskUtil;
import kami.gg.souppvp.util.TasksUtility;
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
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerListeners implements Listener {

    private final Set<UUID> forceInvis = new HashSet<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("modsuite.staff")) {
            this.forceInvis.add(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerUtil.resetPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event){
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            Player player = event.getPlayer();
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() == event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;
            if (profile.getProfileState() == ProfileState.SPAWN && (!(SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player))) && player.getGameMode().equals(GameMode.SURVIVAL)){
                player.sendMessage(CC.translate("&7You no longer have spawn protection!"));
                profile.setProfileState(ProfileState.COMBAT);
                TaskUtil.runLater(() -> {
                    if (player.hasMetadata("noFall")) {
                        player.removeMetadata("noFall", SoupPvP.getInstance());
                    }
                }, 20L);
                if (profile.isJuggernaut()) return;
                Kit kit = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());
                kit.equipKit(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getEntity().hasMetadata("noFall") && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            event.setCancelled(true);
            event.getEntity().removeMetadata("noFall", SoupPvP.getInstance());
        }
    }

    @EventHandler
    public void onPlayerOnSpongeEvent(PlayerMoveEvent event){
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
            Player player = event.getPlayer();
            if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() == event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;
            if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE){
                if (player.hasMetadata("jammed")) return;
                TasksUtility.runTaskLater(() -> {
                    Vector vector = player.getVelocity().setY(+2.5);
                    player.setVelocity(vector);
                    PlayerUtil.playSound(player, Sound.CHICKEN_EGG_POP);
                }, 2L);
            }
        }
    }

    @EventHandler
    public void onDenyMovement(PlayerMoveEvent event){
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
            if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() == event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;
            if (event.getPlayer().hasMetadata("denyMovement")){
                event.setCancelled(true);
            }
        }
    }

}
