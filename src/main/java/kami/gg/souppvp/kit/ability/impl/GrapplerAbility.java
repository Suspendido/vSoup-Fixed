package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class GrapplerAbility implements KitAbility {

    private final ItemStack grapplerRod = new ItemBuilder(Material.FISHING_ROD).name(CC.t("&aGrappler")).build();

    @Override
    public String getName() {
        return "Grappler";
    }

    @Override
    public String getDescription() {
        return "&fHook yourself to locations to travel quickly";
    }

    @Override
    public String getColor() {
        return "&a";
    }

    @Override
    public ItemStack getItem() {
        return grapplerRod.clone();
    }

    private boolean isInvalid(Profile p) {
        return p.isInEvent() || p.getProfileState() == ProfileState.SPAWN;
    }

    @EventHandler
    public void onGrappleLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        if (event.getEntity().getType() != EntityType.FISHING_HOOK) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null || isInvalid(profile)) return;

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Grappler", true)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Grappler", true), true) + "&c."));
            event.setCancelled(true);
        } else {
            // Apply cooldown immediately when launching hook
            SoupPvP.getInstance().getTimersHandler().addPlayerTimer(player.getUniqueId(), new Timer("Grappler", TimeUnit.SECONDS.toMillis(30)), true);
            XPBarTimer.runXpBar(player, 30);
        }
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null || isInvalid(profile)) return;

        if (!player.getItemInHand().isSimilar(grapplerRod)) return;
        if (!event.getHook().isValid()) return;
        if (event.getState() == PlayerFishEvent.State.FISHING) return;

        Entity hooked = event.getHook();
        if (hooked == null) return;

        if (event.getCaught() != null) {
            event.getHook().remove();
            return;
        }

        Location pLoc = player.getLocation();
        Location hookLoc = hooked.getLocation();

        Vector velocity = new Vector(
                hookLoc.getX() - pLoc.getX(),
                hookLoc.getY() - pLoc.getY() + 2.5,
                hookLoc.getZ() - pLoc.getZ()
        ).multiply(0.25);

        player.setVelocity(velocity);
        hooked.remove();

        PlayerUtil.playSound(player, Sound.ORB_PICKUP, 1.0);
    }
}
