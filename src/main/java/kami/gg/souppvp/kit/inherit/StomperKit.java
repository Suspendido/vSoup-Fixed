package kami.gg.souppvp.kit.inherit;

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
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class StomperKit extends Kit {

    private static final String KIT_NAME = "Stomper";
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

    @Override
    public String getName() {
        return KIT_NAME;
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.RARE;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.ANVIL).build();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "&7You depend on locations with high altitudes. You take no fall damage",
                "&7and for each fall damage taken, you deal that amount to whoever you are",
                "&7stomping on top off."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        if (stomperItem == null) {
            stomperItem = new ItemBuilder(Material.ANVIL)
                    .name(CC.translate("&6Stomper"))
                    .build();
        }

        return Arrays.asList(
                new ItemBuilder(Material.IRON_SWORD)
                        .enchantment(Enchantment.DAMAGE_ALL, 1)
                        .enchantment(Enchantment.DURABILITY, 1)
                        .build(),
                stomperItem
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.IRON_BOOTS).build(),
                new ItemBuilder(Material.GOLD_LEGGINGS)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .enchantment(Enchantment.DURABILITY, 10)
                        .build(),
                new ItemBuilder(Material.GOLD_CHESTPLATE)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .enchantment(Enchantment.DURABILITY, 10)
                        .build(),
                new ItemBuilder(Material.LEATHER_HELMET)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .enchantment(Enchantment.DURABILITY, 10)
                        .build()
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

        Profile profile = getProfile(player);
        if (profile == null || !isUsingKit(profile)) return;

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

        Profile profile = getProfile(player);
        if (profile == null || !isUsingKit(profile)) return;

        if (profile.isInEvent() || isInSpawn(player, profile)) {
            player.sendMessage(CC.translate("&cYou can't do this in Spawn."));
            return;
        }

        if (!item.isSimilar(stomperItem != null ? stomperItem : getCombatEquipments().get(1))) return;
        UUID uuid = player.getUniqueId();

        if (hasTimer(uuid)) {
            long remaining = getRemaining(uuid);
            player.sendMessage(ChatColor.RED + "You can't use this for another " +
                    ChatColor.YELLOW + DurationFormatter.getRemaining(remaining, true) +
                    ChatColor.RED + ".");
            return;
        }

        CHARGED_PLAYERS.add(uuid);
        SLAMMING_PLAYERS.remove(uuid);

        player.setVelocity(LAUNCH_VECTOR.clone());

        addTimer(uuid);
        XPBarTimer.runXpBar(player, COOLDOWN_SECONDS);

        player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 1.0F, 1.0F);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        Profile profile = getProfile(player);

        if (profile == null || !isUsingKit(profile)) return;

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
        Profile profile = getProfile(player);

        if (profile == null || !isUsingKit(profile)) return;
        if (profile.isInEvent() || isInSpawn(player, profile)) return;

        if (player.getLocation().getBlockY() >= MAX_Y_LEVEL) {
            player.sendMessage(ChatColor.RED + "Stomper is blocked at this y level.");
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

            Profile targetProfile = getProfile(target);

            if (targetProfile != null && isInSpawn(target, targetProfile)) continue;
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

    private Profile getProfile(Player player) {
        return SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
    }

    private boolean isUsingKit(Profile profile) {
        Kit current = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());
        return current == this;
    }

    private boolean isInSpawn(Player player, Profile profile) {
        return profile.getProfileState() == ProfileState.SPAWN && SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player);
    }

    private boolean hasTimer(UUID uuid) {
        return SoupPvP.getInstance().getTimersHandler().hasTimer(uuid, KIT_NAME + " Charge", true);
    }

    private long getRemaining(UUID uuid) {
        return SoupPvP.getInstance().getTimersHandler().getRemaining(uuid, KIT_NAME + " Charge", true);
    }

    private void addTimer(UUID uuid) {
        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(uuid, new Timer(KIT_NAME + " Charge", COOLDOWN_MILLIS), true);
    }

    public static void cleanup(UUID uuid) {
        CHARGED_PLAYERS.remove(uuid);
        SLAMMING_PLAYERS.remove(uuid);
    }
}