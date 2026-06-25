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
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EnhancerAbility implements KitAbility {

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

        ItemStack hand = player.getItemInHand();
        if (hand == null || hand.getType() != Material.BREWING_STAND_ITEM) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() == null) return;

        event.setCancelled(true);
        player.updateInventory();

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Stim Beacon", true)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Stim Beacon", true), true) + "&c."));
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(player.getUniqueId(), new Timer("Stim Beacon", TimeUnit.SECONDS.toMillis(60)), true);
        XPBarTimer.runXpBar(player, 60);
        PlayerUtil.playSound(player, Sound.CLICK, 1.0);
        BlockUtil.generateTemporaryStimBeacon(event.getClickedBlock().getLocation().add(0, 1, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                    if (!(entity instanceof Player nearby)) continue;

                    Profile nearbyProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(nearby.getUniqueId());

                    if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(nearby)) continue;

                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2));
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0));
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
                }
            }
        }.runTaskLater(SoupPvP.getInstance(), 20L);
    }
}
