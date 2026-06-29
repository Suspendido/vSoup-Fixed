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
import kami.gg.souppvp.util.particles.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AvatarAbility implements KitAbility {

    private final List<Vector> surroundOffsets = Arrays.asList(
            new Vector(1, 1, -1), new Vector(-1, 1, -1),
            new Vector(1, 1, 1), new Vector(-1, 1, 1),
            new Vector(0, 1, 0), new Vector(-1, 1, 0),
            new Vector(1, 1, 0), new Vector(0, 1, -1),
            new Vector(0, 1, 1), new Vector(1, 1, -1),
            new Vector(1, 1, 1), new Vector(-1, 1, 1),
            new Vector(-1, 1, -1), new Vector(1, 1, 0)
    );

    private final ArrayList<BlockState> toRollback = new ArrayList<>();

    private final Timer waterGunTimer;
    private final Timer firejumpTimer;

    public AvatarAbility() {
        this.firejumpTimer = new Timer("Avatar Jump", TimeUnit.SECONDS.toMillis(15));
        this.waterGunTimer = new Timer("Water Gun", TimeUnit.SECONDS.toMillis(30));
        SoupPvP.getInstance().getTimerManager().registerTimer(firejumpTimer);
        SoupPvP.getInstance().getTimerManager().registerTimer(waterGunTimer);
    }

    @Override
    public String getName() {
        return "Avatar";
    }

    @Override
    public String getDescription() {
        return "&fWater gun slows enemies + jump sets them on fire";
    }

    @Override
    public String getColor() {
        return "&b";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.INK_SACK).name("&bWater Gun").durability(12).build();
    }

    private boolean isAvatar(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        return hasAbility(player, profile, getName());
    }

    private boolean isInSafe(Profile p) {
        return p.isInEvent() || p.getProfileState() == ProfileState.SPAWN;
    }

    private List<Location> getSurrounding(Location loc) {
        List<Location> list = new ArrayList<>();
        for (Vector offset : surroundOffsets) {
            list.add(loc.clone().add(offset));
        }
        return list;
    }

    private void rollback(BlockState state) {
        if (state instanceof Sign sign) {
            Sign s = (Sign) state.getLocation().getBlock().getState();
            for (int i = 0; i < 4; i++) s.setLine(i, sign.getLine(i));
            s.update(true);
        } else {
            state.update(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!isAvatar(player)) return;

        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setDamage(e.getDamage() / 2.2);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null || !isAvatar(killer)) return;

        for (ItemStack armor : killer.getInventory().getArmorContents()) {
            if (armor != null) {
                armor.setDurability((short) Math.max(0, armor.getDurability() - 15));
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(p.getUniqueId());
        if (isInSafe(profile) || !isAvatar(p)) return;

        ItemStack item = e.getItem();
        if (item == null) return;

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (!AbilityItemComparator.isSameAbilityItem(item, getItem())) return;

            // cooldown
            if (waterGunTimer.hasTimer(p)) {
                p.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(waterGunTimer.getRemaining(p), true) + "&c."));
                return;
            }

            waterGunTimer.applyTimer(p);
            XPBarTimer.runXpBar(p, 30);

            FallingBlock b = p.getWorld().spawnFallingBlock(p.getEyeLocation(), Material.STAINED_GLASS, (byte) 3);

            b.setDropItem(false);
            b.setVelocity(p.getEyeLocation().getDirection().multiply(2.3).add(new Vector(0, 0.3, 0)));
            b.setMetadata("avatar", new FixedMetadataValue(SoupPvP.getInstance(), p.getUniqueId()));
            p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 1, 1);

            // runnable de impacto
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!b.isValid() || b.isDead() || !p.isOnline()) {
                        cancel();
                        return;
                    }

                    for (Entity ent : b.getNearbyEntities(3, 3, 3)) {
                        if (!(ent instanceof Player target) || target == p) continue;

                        Profile targetP = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());
                        if (targetP.getProfileState() == ProfileState.SPAWN) continue;

                        b.remove();
                        cancel();

                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 4));

                        Location loc = target.getLocation();
                        Block base = loc.getBlock();
                        if (base.isLiquid()) loc = loc.add(0, 1, 0);

                        List<BlockState> states = new ArrayList<>();

                        for (Location scan : getSurrounding(loc)) {
                            Block blk = scan.getBlock();
                            if (blk.getType() == Material.AIR) {
                                states.add(blk.getState());
                                blk.setType(Material.STATIONARY_WATER);
                            }
                        }

                        toRollback.addAll(states);

                        Bukkit.getScheduler().runTaskLater(SoupPvP.getInstance(), () -> {
                            for (BlockState st : states) {
                                rollback(st);
                                toRollback.remove(st);
                            }
                        }, 120L);

                        break;
                    }
                }
            }.runTaskTimer(SoupPvP.getInstance(), 2L, 2L);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(p.getUniqueId());
        if (!e.isSneaking() || isInSafe(profile) || !isAvatar(p) || p.isOnGround()) return;

        // cd
        if (firejumpTimer.hasTimer(p)) {
            p.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(firejumpTimer.getRemaining(p), true) + "&c."));
            return;
        }

        firejumpTimer.applyTimer(p);

        p.setMetadata("avatar", new FixedMetadataValue(SoupPvP.getInstance(), true));

        p.setVelocity(p.getEyeLocation().getDirection().multiply(1.9));
        p.playSound(p.getLocation(), Sound.GHAST_FIREBALL, 1, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline() || p.isOnGround()) {
                    try {
                        p.removeMetadata("avatar", SoupPvP.getInstance());
                    } catch (Exception ignore) {}
                    cancel();
                    return;
                }

                p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);

                p.getNearbyEntities(1, 1, 1).stream()
                        .filter(ent -> ent instanceof Player)
                        .forEach(ent -> ent.setFireTicks(100));
            }
        }.runTaskTimer(SoupPvP.getInstance(), 0L, 1L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!p.hasMetadata("avatar")) return;

        try {
            ParticleEffect.HUGE_EXPLOSION.sendToPlayers(Bukkit.getOnlinePlayers(), p.getLocation(), 0, 0, 0, 0, 1);
        } catch (Exception ignored) {}

        if (p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
            p.removeMetadata("avatar", SoupPvP.getInstance());
        }
    }

    @EventHandler
    public void onChange(EntityChangeBlockEvent e) {
        if (e.getEntityType() == EntityType.FALLING_BLOCK && e.getEntity().hasMetadata("avatar")) {
            e.getEntity().remove();
            e.setCancelled(true);
        }
    }
}
