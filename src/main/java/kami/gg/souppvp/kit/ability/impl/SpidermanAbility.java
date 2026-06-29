package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpidermanAbility implements KitAbility {

    private static final Vector[] WEB_OFFSETS = new Vector[]{
            new Vector(1, 1, -1), new Vector(-1, 1, -1),
            new Vector(1, 1, 1), new Vector(-1, 1, 1),
            new Vector(0, 1, 0),
            new Vector(-1, 1, 0), new Vector(1, 1, 0),
            new Vector(0, 1, -1), new Vector(0, 1, 1)
    };

    private final Timer webTimer;

    public SpidermanAbility() {
        this.webTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(45));
        SoupPvP.getInstance().getTimerManager().registerTimer(webTimer);
    }

    @Override
    public String getName() {
        return "Spiderman";
    }

    @Override
    public String getDescription() {
        return "&fShoot web to trap enemies in cobwebs";
    }

    @Override
    public String getColor() {
        return "&d";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.WEB).name("&dWeb Shooter").build();
    }

    private List<Location> getSurrounding(Location loc) {
        List<Location> locations = new ArrayList<>(WEB_OFFSETS.length);
        for (Vector v : WEB_OFFSETS) {
            locations.add(loc.clone().add(v));
        }
        return locations;
    }

    private void restoreBlock(BlockState bs) {
        if (bs instanceof Sign signBefore) {
            Location loc = signBefore.getLocation();
            Block block = loc.getBlock();

            block.setType(signBefore.getType());

            Sign signNow = (Sign) block.getState();
            for (int i = 0; i < 4; i++) {
                signNow.setLine(i, signBefore.getLine(i));
            }
            signNow.update(true);
            return;
        }
        bs.update(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(p.getUniqueId());

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!hasAbility(p, profile, getName())) return;

        ItemStack hand = p.getItemInHand();
        if (!AbilityItemComparator.isSameAbilityItem(hand, getItem())) return;

        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        e.setCancelled(true);

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(p)) {
            p.sendMessage(CC.t("&cYou can't use this in spawn."));
            return;
        }

        if (webTimer.hasTimer(p)) {
            p.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(webTimer.getRemaining(p), true) + "&c."));
            return;
        }

        webTimer.applyTimer(p);
        XPBarTimer.runXpBar(p, 45);

        FallingBlock fb = p.getWorld().spawnFallingBlock(
                p.getEyeLocation(),
                Material.WEB,
                (byte) 0
        );

        fb.setMetadata("spiderman", new FixedMetadataValue(SoupPvP.getInstance(), p.getUniqueId()));
        fb.setDropItem(false);
        fb.setVelocity(p.getEyeLocation().getDirection().multiply(2.3).add(new Vector(0, 0.3, 0)));
        p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 1f, 1f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!fb.isValid() || fb.isDead() || !p.isOnline()) {
                    cancel();
                    return;
                }

                for (Entity ent : fb.getNearbyEntities(3, 3, 3)) {
                    if (!(ent instanceof Player found)) continue;
                    if (found == p) continue;

                    Profile pfFound = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(found.getUniqueId());
                    if (pfFound.getProfileState() == ProfileState.SPAWN) continue;

                    fb.remove();
                    cancel();

                    Location loc = found.getLocation();
                    if (loc.getBlock().getType() == Material.WEB) loc.add(0, 1, 0);

                    List<BlockState> placed = new ArrayList<>();

                    for (Location scan : getSurrounding(loc)) {
                        Block b = scan.getBlock();
                        if (b.getType() != Material.AIR) continue;
                        placed.add(b.getState());
                        b.setType(Material.WEB);
                    }

                    Bukkit.getScheduler().runTaskLater(SoupPvP.getInstance(), () -> {
                        for (BlockState bs : placed) restoreBlock(bs);
                    }, 120L);
                    return;
                }
            }
        }.runTaskTimer(SoupPvP.getInstance(), 2L, 2L);
    }

    @EventHandler
    public void onFallingBlockLand(EntityChangeBlockEvent e) {
        if (!(e.getEntity() instanceof FallingBlock fb)) return;
        if (!fb.hasMetadata("spiderman")) return;

        e.setCancelled(true);
        fb.remove();
    }
}
