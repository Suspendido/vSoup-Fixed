package kami.gg.souppvp.kit.ability.impl;

import com.google.common.collect.Lists;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.XPBarTimer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class VampireAbility implements KitAbility {

    @Override
    public String getName() {
        return "Vampire";
    }

    @Override
    public String getDescription() {
        return "&fShoot bats to damage enemies + Regen V on kill";
    }

    @Override
    public String getColor() {
        return "&8";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.MONSTER_EGG)
                .durability((short) 65)
                .name("&8&lBat Blast")
                .lore(
                        "&7Right-click to shoot bats",
                        "&7Bats damage enemies",
                        "&7Kill bonus: Regen V for 10s"
                )
                .build();
    }

    @Override
    public void onKitSelect(Player player) {
        // Nothing special
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (item == null || item.getType() != Material.MONSTER_EGG || item.getDurability() != 65) return;
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        event.setCancelled(true);

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.t("&cYou can't do this in Spawn."));
            return;
        }

        if (player.hasMetadata("requireLand")) {
            player.sendMessage(ChatColor.RED + "You must land on the ground once you leave Spawn to use this!");
            return;
        }

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Bat Blast", true)) {
            player.sendMessage(ChatColor.RED + "You can't use this for another " + ChatColor.YELLOW + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Bat Blast", true), true) + ChatColor.RED + ".");
            return;
        }

        // Apply cooldown immediately
        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(player.getUniqueId(), new Timer("Bat Blast", TimeUnit.SECONDS.toMillis(45)), true);
        XPBarTimer.runXpBar(player, 45);
        player.getLocation().getWorld().playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1F, 1F);

        // Spawn bats
        List<Entity> entities = Lists.newArrayList();
        for (int i = 1; i <= 6; i++) {
            entities.add(player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT));
        }

        new BukkitRunnable() {
            final long stayTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.toMillis(2000L);
            final long boostTime = System.currentTimeMillis() + 100L;
            final Location location = player.getEyeLocation().clone();

            @Override
            public void run() {
                if (System.currentTimeMillis() >= this.stayTime) {
                    this.cancel();
                    entities.forEach(Entity::remove);
                    return;
                }

                entities.stream()
                        .filter(Entity::isValid)
                        .forEach(bat -> {
                            if (System.currentTimeMillis() < this.boostTime) {
                                bat.setVelocity(this.location.getDirection().clone().multiply(1.9));
                            } else {
                                bat.setVelocity(this.location.getDirection().clone().multiply(0.4));
                            }

                            bat.getNearbyEntities(3, 3, 3)
                                    .stream()
                                    .filter(entity -> entity instanceof Player && !SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(entity))
                                    .map(Player.class::cast)
                                    .filter(found -> !found.getUniqueId().equals(player.getUniqueId()))
                                    .forEach(found -> {
                                        bat.setVelocity(bat.getVelocity().add(new Vector(0, 0.5, 0)));
                                        bat.getWorld().playSound(bat.getLocation(), Sound.BAT_HURT, 0.1F, 0.1F);

                                        found.setVelocity(bat.getVelocity().clone().add(new Vector(0, 0.07, 0)));

                                        if (MinecraftServer.currentTick % 3 == 0) {
                                            found.damage(10, player);
                                        }
                                    });
                        });
            }
        }.runTaskTimer(SoupPvP.getInstance(), 1L, 1L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());

        if (!killer.getUniqueId().equals(event.getEntity().getUniqueId())) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 4));
        }
    }
}
