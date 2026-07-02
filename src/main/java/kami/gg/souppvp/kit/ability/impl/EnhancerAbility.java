package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.BlockUtil;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EnhancerAbility implements KitAbility {
    private final Timer stimBeaconTimer;
    private final Map<UUID, Map<PotionEffectType, PotionEffect>> originalEffects = new HashMap<>();

    public EnhancerAbility() {
        this.stimBeaconTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(60));
        SoupPvP.getInstance().getTimerManager().registerTimer(stimBeaconTimer);
    }

    @Override
    public String getName() {
        return "Enhancer";
    }

    @Override
    public String getDescription() {
        return "&fPlace stim beacon to buff nearby enemies";
    }

    @Override
    public String getColor() {
        return "&d";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.BREWING_STAND_ITEM)
                .name("&dStim Beacon")
                .build();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.isInEvent()) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!hasAbility(player, profile, getName())) return;

        ItemStack hand = player.getItemInHand();
        if (hand == null || hand.getType() != Material.BREWING_STAND_ITEM) return;

        event.setCancelled(true);
        player.updateInventory();

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        if (stimBeaconTimer.hasTimer(player)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(stimBeaconTimer.getRemaining(player), true) + "&c."));
            return;
        }

        Location beaconLocation;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            beaconLocation = event.getClickedBlock().getLocation().add(0, 1, 0);
        } else {
            beaconLocation = player.getLocation().add(0, 1, 0);
        }

        stimBeaconTimer.applyTimer(player);
        XPBarTimer.runXpBar(player, 60);
        PlayerUtil.playSound(player, Sound.CLICK, 1.0);
        BlockUtil.generateTemporaryStimBeacon(beaconLocation);

        new BukkitRunnable() {
            private int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 100) {
                    // Restore original effects when timer ends
                    for (UUID uuid : originalEffects.keySet()) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) {
                            restoreOriginalEffects(p);
                        }
                    }
                    originalEffects.clear();
                    this.cancel();
                    return;
                }

                // Apply effects to nearby players
                for (Entity entity : beaconLocation.getWorld().getNearbyEntities(beaconLocation, 5, 5, 5)) {
                    if (!(entity instanceof Player nearby)) continue;

                    Profile nearbyProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(nearby.getUniqueId());
                    if (nearbyProfile == null) continue;

                    if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(nearby)) continue;

                    UUID uuid = nearby.getUniqueId();
                    if (!originalEffects.containsKey(uuid)) {
                        saveOriginalEffects(nearby);
                    }

                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2), true);
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 0), true);
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0), true);
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0), true);
                }

                // Show particle indicator for the area
                if (ticks % 5 == 0) {
                    showParticleCircle(beaconLocation);
                }

                ticks++;
            }
        }.runTaskTimer(SoupPvP.getInstance(), 20L, 1L);
    }

    private void showParticleCircle(Location center) {
        for (int i = 0; i < 360; i += 30) {
            double radians = Math.toRadians(i);
            double x = center.getX() + 5 * Math.cos(radians);
            double z = center.getZ() + 5 * Math.sin(radians);

            Location particleLoc = new Location(center.getWorld(), x, center.getY(), z);
            center.getWorld().playEffect(particleLoc, Effect.MOBSPAWNER_FLAMES, 0);
        }

        // Show particles at different heights
        for (int y = -2; y <= 2; y++) {
            Location heightLoc = center.clone().add(0, y, 0);
            for (int i = 0; i < 360; i += 45) {
                double radians = Math.toRadians(i);
                double x = heightLoc.getX() + 5 * Math.cos(radians);
                double z = heightLoc.getZ() + 5 * Math.sin(radians);

                Location particleLoc = new Location(heightLoc.getWorld(), x, heightLoc.getY(), z);
                center.getWorld().playEffect(particleLoc, Effect.MOBSPAWNER_FLAMES, 0);
            }
        }
    }

    private void saveOriginalEffects(Player player) {
        Map<PotionEffectType, PotionEffect> effects = new HashMap<>();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            effects.put(effect.getType(), effect);
        }
        originalEffects.put(player.getUniqueId(), effects);
    }

    private void restoreOriginalEffects(Player player) {
        Map<PotionEffectType, PotionEffect> effects = originalEffects.get(player.getUniqueId());
        if (effects == null) return;

        // Remove all current effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        // Restore original effects
        for (PotionEffect effect : effects.values()) {
            player.addPotionEffect(effect);
        }
    }
}
