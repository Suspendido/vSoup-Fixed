package kami.gg.souppvp.kit.kits;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.*;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CyclopsKit extends Kit {

    private static final Random RANDOM = new Random();
    private static final String LASER_NAME = "Laser Beam";
    private static final int LASER_COOLDOWN = 45;
    private static final int LASER_TICKS = 30;
    private static final double LASER_CHANCE_RED = 0.5;

    @Override
    public String getName() {
        return "Cyclops";
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
        return new ItemBuilder(Material.REDSTONE).build();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "&7Laser beam enemies with penetrable blocks to set",
                "&7the exciting fights straight and scare off enemies."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return Arrays.asList(
                new ItemBuilder(Material.IRON_SWORD)
                        .enchantment(Enchantment.DAMAGE_ALL, 1)
                        .enchantment(Enchantment.DURABILITY, 3)
                        .build(),
                new ItemBuilder(Material.REDSTONE).name("&cLaser Beam").build()
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.IRON_BOOTS).build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS)
                        .enchantment(Enchantment.DURABILITY, 10)
                        .build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .color(Color.RED)
                        .enchantment(Enchantment.DURABILITY, 10)
                        .build(),
                new ItemBuilder(Material.IRON_HELMET).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }

    @Override
    public void onSelect(Player player) {

    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        ItemStack wandItem = getCombatEquipments().get(1);
        ItemStack item = event.getItem();

        if (!profile.getCurrentKit().equals(getName())) return;
        if (!isRightClick(event.getAction())) return;
        if (item == null) return;
        if (!item.isSimilar(wandItem)) return;

        event.setCancelled(true);
        player.updateInventory();

        if (isInSpawn(player, profile)) {
            player.sendMessage("&cYou can't use this while in spawn!");
            return;
        }

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), LASER_NAME, true)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(getRemaining(player.getUniqueId()), true) + "&c."));
            return;
        }

        // Apply cooldown
        addTimer(player.getUniqueId(), TimeUnit.SECONDS.toMillis(LASER_COOLDOWN));
        XPBarTimer.runXpBar(player, LASER_COOLDOWN);
        PlayerUtil.playSound(player, Sound.GHAST_SCREAM, 1.0);
        player.getNearbyEntities(5, 5, 5)
                .stream()
                .filter(e -> e instanceof Player)
                .forEach(e -> PlayerUtil.playSound((Player) e, Sound.ZOMBIE_REMEDY, 1.0));

        // Start laser bursts
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ >= LASER_TICKS || !player.isOnline()) {
                    cancel();
                    return;
                }

                boolean red = RANDOM.nextDouble() <= LASER_CHANCE_RED;
                spawnLaserBeam(player, red);
            }
        }.runTaskTimer(SoupPvP.getInstance(), 1L, 1L);
    }

    private void spawnLaserBeam(Player player, boolean red) {
        new BukkitRunnable() {
            final Vector direction = player.getEyeLocation().getDirection().normalize();
            final Location loc = player.getEyeLocation().clone();
            int steps = 0;

            @Override
            public void run() {

                if (!player.isOnline() || steps++ > 25) {
                    cancel();
                    return;
                }

                loc.add(direction.clone().multiply(0.7));

                // Spawn particle
                player.getWorld().spigot().playEffect(
                        loc,
                        Effect.COLOURED_DUST,
                        0,
                        0,
                        red ? 1.0f : 0.2f,
                        0.0f,
                        0.0f,
                        1.0f,
                        0,
                        16
                );

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 0.7, 0.7, 0.7)) {
                    if (!(entity instanceof Player target)) continue;
                    if (target == player) continue;

                    Profile p = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());

                    if (p.getProfileState() == ProfileState.SPAWN) continue;

                    target.damage(4.0, player);
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(SoupPvP.getInstance(), 0L, 1L);
    }


    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getEntity().hasMetadata("laser_beam")) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }
}
