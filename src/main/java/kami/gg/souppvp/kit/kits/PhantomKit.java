package kami.gg.souppvp.kit.kits;

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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhantomKit extends Kit {

    private static final ItemStack PHANTOM_FEATHER = new ItemBuilder(Material.FEATHER).name(CC.t("&7Phantom")).build();

    @Override
    public String getName() {
        return "Phantom";
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
        return new ItemBuilder(Material.FEATHER).build();
    }

    @Override
    public List<String> getDescription() {
        return List.of(
                "&7Fly high into the sky to escape from awkward",
                "&7situations."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return List.of(
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .enchantment(Enchantment.DAMAGE_ALL, 1)
                        .build(),
                PHANTOM_FEATHER
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.LEATHER_BOOTS).color(Color.WHITE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 10).build(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).color(Color.WHITE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 10).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.WHITE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 10).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(Color.WHITE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 10).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return List.of(
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)
        );
    }

    @Override
    public void onSelect(Player player) {}

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        // Validación instantánea
        if (item == null || !item.isSimilar(PHANTOM_FEATHER)) return;

        Action act = event.getAction();
        if (act != Action.RIGHT_CLICK_AIR && act != Action.RIGHT_CLICK_BLOCK) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent()) return;

        Kit phantom = SoupPvP.getInstance().getKitsHandler().getKitByName("Phantom");
        Kit current = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());
        if (current != phantom) return;

        event.setCancelled(true);

        if (isInSpawn(player, profile)) {
            player.sendMessage(CC.t("&cYou can't use this while in spawn."));
            return;
        }

        if (hasTimer(player.getUniqueId())) {
            player.sendMessage(CC.t("&cYou can't use this for another " + DurationFormatter.getRemaining(getRemaining(player.getUniqueId()), true) + "&c."));
            return;
        }

        addTimer(player.getUniqueId(), TimeUnit.SECONDS.toMillis(30));
        XPBarTimer.runXpBar(player, 30);

        player.playSound(player.getLocation(), Sound.WITHER_SHOOT, 1F, 1F);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 1));

        for (Entity e : player.getNearbyEntities(5, 5, 5)) {
            if (e instanceof Player nearby) {
                PlayerUtil.playSound(nearby, Sound.ENDERMAN_STARE, 1.0);
            }
        }

        TasksUtility.runTaskLater(() -> {
            player.setFlying(false);
            player.setAllowFlight(false);
        }, 20 * 5);
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Kit current = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());

        if (current == SoupPvP.getInstance().getKitsHandler().getKitByName("Phantom")) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }
}
