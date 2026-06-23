package kami.gg.souppvp.kit.kits;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
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

public class AvatarKit extends Kit {

    private final Kit selfKit = this;
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

    @Override
    public String getName() {
        return "Avatar";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.LEGENDARY;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.INK_SACK).durability(12).build();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "&7Have the ability to control elements. Shoot water",
                "&7guns to slow enemies. Jump over enemies to set",
                "&7them on fire."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return Arrays.asList(
                new ItemBuilder(Material.DIAMOND_SWORD).build(),
                new ItemBuilder(Material.INK_SACK)
                        .name(CC.t("&bWater Gun"))
                        .durability((short) 12)
                        .build()
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.IRON_BOOTS).build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).build(),
                new ItemBuilder(Material.CHAINMAIL_HELMET).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return Collections.singletonList(
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0)
        );
    }

    @Override
    public void onSelect(Player player) {

    }

    private boolean isAvatar(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null || profile.getCurrentKit() == null) return false;
        return profile.getCurrentKit().equalsIgnoreCase(getName());
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

            ItemStack gun = getCombatEquipments().get(1);
            if (!item.isSimilar(gun)) return;

            // cooldown
            if (SoupPvP.getInstance().getTimersHandler().hasTimer(p.getUniqueId(), "Water Gun", true)) {
                long rem = SoupPvP.getInstance().getTimersHandler().getRemaining(p.getUniqueId(), "Water Gun", true);
                p.sendMessage(ChatColor.RED + "You can't use this for another " + ChatColor.YELLOW +
                        DurationFormatter.getRemaining(rem, true) + ChatColor.RED + ".");
                return;
            }

            // crear proyectil
            FallingBlock b = p.getWorld().spawnFallingBlock(
                    p.getEyeLocation(),
                    Material.STAINED_GLASS, (byte) 3
            );
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

            SoupPvP.getInstance().getTimersHandler().addPlayerTimer(
                    p.getUniqueId(),
                    new Timer("Water Gun", 30_000),
                    true
            );

            XPBarTimer.runXpBar(p, 30);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(p.getUniqueId());
        if (!e.isSneaking() || isInSafe(profile) || !isAvatar(p) || p.isOnGround()) return;

        // cd
        if (SoupPvP.getInstance().getTimersHandler().hasTimer(p.getUniqueId(), "Avatar Jump", false)) {
            long rem = SoupPvP.getInstance().getTimersHandler().getRemaining(p.getUniqueId(), "Avatar Jump", false);
            p.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(rem, true) + "&c."));
            return;
        }

        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(
                p.getUniqueId(),
                new Timer("Avatar Jump", 15_000),
                false
        );

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
            ParticleEffect.HUGE_EXPLOSION.sendToPlayers(Bukkit.getOnlinePlayers(), p.getLocation(), 0,0,0,0, 1);
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
