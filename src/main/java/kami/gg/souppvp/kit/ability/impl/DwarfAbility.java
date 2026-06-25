package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.XPBarTimer;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DwarfAbility implements KitAbility {

    @Getter
    private static final Map<UUID, Float> chargeUp = new HashMap<>();
    private static final Map<UUID, Boolean> fullChargeNotified = new HashMap<>();

    @Override
    public String getName() {
        return "Dwarf";
    }

    @Override
    public String getDescription() {
        return "&fCrouch to charge up knockback blast";
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.GOLD_AXE)
                .name(ChatColor.BLUE + "Dwarf's Axe")
                .enchantment(Enchantment.DAMAGE_ALL, 3)
                .enchantment(Enchantment.DURABILITY, 10)
                .build();
    }

    @Override
    public void onKitSelect(Player player) {
        chargeUp.put(player.getUniqueId(), 0F);
        fullChargeNotified.put(player.getUniqueId(), false);
    }

    @Override
    public void onKitDeselect(Player player) {
        chargeUp.remove(player.getUniqueId());
        fullChargeNotified.remove(player.getUniqueId());
    }

    public void setup() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
                    if (profile == null) continue;

                    if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) continue;
                    if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Charged Up", true)) continue;

                    float charge = chargeUp.getOrDefault(player.getUniqueId(), 0F);
                    boolean wasFullyCharged = charge >= 1.0F;

                    if (player.isSneaking()) {
                        charge = Math.min(1.0F, charge + 0.1F);

                        if (charge >= 1.0F && !wasFullyCharged) {
                            player.sendMessage(CC.t("&6&lYou are fully Charged Up!"));
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                            fullChargeNotified.put(player.getUniqueId(), true);
                        } else if (charge < 1.0F) {
                            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, charge + 0.5f);
                        }

                    } else {
                        charge = Math.max(0F, charge - 0.1F);

                        if (charge < 1.0F) {
                            fullChargeNotified.put(player.getUniqueId(), false);
                        }
                    }

                    chargeUp.put(player.getUniqueId(), charge);
                    player.setExp(charge);
                }
            }
        }.runTaskTimer(SoupPvP.getInstance(), 0L, 10L);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(damager.getUniqueId());
        if (profile == null) return;


        if (chargeUp.getOrDefault(damager.getUniqueId(), 0F) < 1.0F) return;

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(damager.getUniqueId(), "Charged Up", true)) {
            damager.sendMessage(CC.t("&cYou can't use this yet!"));
            return;
        }

        if (profile.getProfileState() == ProfileState.SPAWN) {
            damager.sendMessage(CC.t("&cYou can't use this in Spawn."));
            return;
        }

        Vector velocity = victim.getLocation().toVector()
                .subtract(damager.getLocation().toVector())
                .normalize()
                .multiply(1.2)
                .setY(1.0);

        Bukkit.getScheduler().runTask(SoupPvP.getInstance(), () -> {
            damager.setExp(0F);
            damager.setLevel(0);

            victim.setVelocity(velocity);

            victim.getWorld().playEffect(victim.getLocation(), Effect.EXPLOSION_HUGE, 1, 10);
            victim.playSound(victim.getLocation(), Sound.EXPLODE, 1f, 1f);
            damager.playSound(damager.getLocation(), Sound.EXPLODE, 1f, 0.8f);

            SoupPvP.getInstance().getTimersHandler().addPlayerTimer(
                    damager.getUniqueId(),
                    new Timer("Charged Up", TimeUnit.SECONDS.toMillis(10)),
                    true
            );
            XPBarTimer.runXpBar(damager, 10);

            chargeUp.put(damager.getUniqueId(), 0F);
            fullChargeNotified.put(damager.getUniqueId(), false);
        });
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;


        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDamage(event.getDamage() / 3.0);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UUID uuid = event.getEntity().getUniqueId();
        chargeUp.remove(uuid);
        fullChargeNotified.remove(uuid);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        chargeUp.remove(uuid);
        fullChargeNotified.remove(uuid);
    }
}
