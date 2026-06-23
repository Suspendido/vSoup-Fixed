package kami.gg.souppvp.kit.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.*;
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

public class EnhancerKit extends Kit {

    @Override
    public String getName() {
        return "Enhancer";
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
        return new ItemBuilder(Material.BREWING_STAND_ITEM).build();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "&7Gain access to a portable effects enhancer to gain advantages",
                "&7above other players through effects like strength, regeneration, etc."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return Arrays.asList(
                new ItemBuilder(Material.IRON_SWORD)
                        .enchantment(Enchantment.DAMAGE_ALL, 1)
                        .enchantment(Enchantment.DURABILITY, 3)
                        .build(),

                new ItemBuilder(Material.BREWING_STAND_ITEM)
                        .name("&dStim Beacon")
                        .build()
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.GOLD_BOOTS)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                        .enchantment(Enchantment.DURABILITY, 10)
                        .build(),

                new ItemBuilder(Material.LEATHER_LEGGINGS)
                        .color(Color.BLACK)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchantment(Enchantment.DURABILITY, 3)
                        .build(),

                new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .color(Color.BLACK)
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                        .enchantment(Enchantment.DURABILITY, 3)
                        .build(),

                null
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return List.of(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
    }

    @Override
    public void onSelect(Player player) {}

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.isInEvent()) return;
        if (!profile.getCurrentKit().equals(getName())) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack hand = player.getItemInHand();
        if (hand == null || hand.getType() != Material.BREWING_STAND_ITEM) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() == null) return;

        event.setCancelled(true);
        player.updateInventory();

        if (hasTimer(player.getUniqueId())) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(getRemaining(player.getUniqueId()), true) + "&c."));
            return;
        }

        if (isInSpawn(player, profile)) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        addTimer(player.getUniqueId(), TimeUnit.SECONDS.toMillis(60));
        XPBarTimer.runXpBar(player, 60);
        PlayerUtil.playSound(player, Sound.CLICK, 1.0);
        BlockUtil.generateTemporaryStimBeacon(event.getClickedBlock().getLocation().add(0, 1, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                    if (!(entity instanceof Player nearby)) continue;

                    Profile nearbyProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(nearby.getUniqueId());

                    if (nearbyProfile.getCurrentKit().equals(getName())) continue;
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
