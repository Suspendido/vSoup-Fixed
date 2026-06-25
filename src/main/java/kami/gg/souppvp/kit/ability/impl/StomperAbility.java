package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class StomperAbility implements KitAbility {

    private static final long COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(25);
    private static final int COOLDOWN_SECONDS = 25;
    private static final double LAUNCH_VELOCITY = 1.7;
    private static final double SLAM_VELOCITY = -6.0;
    private static final int MAX_Y_LEVEL = 176;
    private static final int DAMAGE_RADIUS = 3;
    private static final int SNEAKING_DAMAGE = 6;
    private static final int BLOCKING_DAMAGE = 6;
    private static final int NORMAL_DAMAGE = 12;
    private static final double FALL_DAMAGE_REDUCTION = 0.5;

    private static final Vector LAUNCH_VECTOR = new Vector(0, LAUNCH_VELOCITY, 0);
    private static final Vector SLAM_VECTOR = new Vector(0, SLAM_VELOCITY, 0);

    private ItemStack stomperItem;

    private static final Set<UUID> CHARGED_PLAYERS = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> SLAMMING_PLAYERS = ConcurrentHashMap.newKeySet();

    public StomperAbility() {
        this.stomperItem = new ItemBuilder(Material.ANVIL).name("&6Stomper").build();
    }

    @Override
    public String getName() {
        return "Stomper";
    }

    @Override
    public String getDescription() {
        return "&fLaunch up and slam down to deal AOE damage";
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public ItemStack getItem() {
        return stomperItem.clone();
    }

    @Override
    public void onKitSelect(Player player) {
        CHARGED_PLAYERS.remove(player.getUniqueId());
        SLAMMING_PLAYERS.remove(player.getUniqueId());
    }

    @Override
    public void onKitDeselect(Player player) {
        CHARGED_PLAYERS.remove(player.getUniqueId());
        SLAMMING_PLAYERS.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWater(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!CHARGED_PLAYERS.contains(uuid)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;

        if (event.getTo().getBlock().isLiquid()) {
            CHARGED_PLAYERS.remove(uuid);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.ANVIL) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;

        if (profile.isInEvent() || SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.t("&cYou can't do this in Spawn."));
            return;
        }

        if (!item.isSimilar(stomperItem)) return;
        UUID uuid = player.getUniqueId();

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(uuid, "Stomper", true)) {
            long remaining = SoupPvP.getInstance().getTimersHandler().getRemaining(uuid, "Stomper", true);
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(remaining, true) + "&c."));
            return;
        }

        // Apply cooldown immediately
        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(uuid, new Timer("Stomper", COOLDOWN_MILLIS), true);
        XPBarTimer.runXpBar(player, COOLDOWN_SECONDS);

        CHARGED_PLAYERS.add(uuid);
        SLAMMING_PLAYERS.remove(uuid);

        player.setVelocity(LAUNCH_VECTOR.clone());
        player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 1.0F, 1.0F);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        Profile profile = SoupPvP .getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile == null) return;

        UUID uuid = player.getUniqueId();
        CHARGED_PLAYERS.remove(uuid);

        if (SLAMMING_PLAYERS.contains(uuid)) {
            handleSlamDamage(player, event);
            SLAMMING_PLAYERS.remove(uuid);
        } else {
            event.setDamage(event.getDamage() * FALL_DAMAGE_REDUCTION);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile == null) return;
        if (profile.isInEvent() || SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) return;

        if (player.getLocation().getBlockY() >= MAX_Y_LEVEL) {
            player.sendMessage(CC.t("&cStomper is blocked at this y level."));
            return;
        }

        UUID uuid = player.getUniqueId();

        if (!CHARGED_PLAYERS.contains(uuid)) return;
        player.setVelocity(SLAM_VECTOR.clone());

        CHARGED_PLAYERS.remove(uuid);
        SLAMMING_PLAYERS.add(uuid);
    }

    private void handleSlamDamage(Player player, EntityDamageEvent event) {
        Location location = player.getLocation();
        World world = player.getWorld();
        double damage = event.getDamage();

        for (Entity entity : player.getNearbyEntities(DAMAGE_RADIUS, DAMAGE_RADIUS, DAMAGE_RADIUS)) {
            if (!(entity instanceof LivingEntity)) continue;
            if (!(entity instanceof Player target)) continue;

            Profile targetProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());

            if (targetProfile != null && SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(target)) continue;
            double finalDamage = calculateDamage(target, damage);
            ((LivingEntity) entity).damage(finalDamage, player);
        }

        event.setCancelled(true);
        world.playSound(location, Sound.EXPLODE, 1.0F, 1.0F);
        world.playEffect(location, Effect.EXPLOSION_HUGE, 2);
    }

    private double calculateDamage(Player target, double baseDamage) {
        if (target.isSneaking()) {
            return target.isBlocking() ? BLOCKING_DAMAGE : SNEAKING_DAMAGE;
        }
        return Math.min(NORMAL_DAMAGE, baseDamage);
    }

    public static void cleanup(UUID uuid) {
        CHARGED_PLAYERS.remove(uuid);
        SLAMMING_PLAYERS.remove(uuid);
    }
}
