package kami.gg.souppvp.kit.kits;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class ArcherKit extends Kit {

    private final Map<UUID, Location> shotLocations = new HashMap<>();

    @Override
    public String getName() {
        return "Archer";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.UNCOMMON;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.BOW).build();
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                "&7Become robin hood and snipe enemies.",
                "&7Longer distance = more damage."
        );
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        return Arrays.asList(
                new ItemBuilder(Material.IRON_SWORD).enchantment(Enchantment.DURABILITY, 2).build(),
                new ItemBuilder(Material.BOW).enchantment(Enchantment.DURABILITY, 3).enchantment(Enchantment.ARROW_INFINITE, 1).build(),
                new ItemBuilder(Material.ARROW).amount(64).build()
        );
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[] {
                new ItemBuilder(Material.LEATHER_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 10).build(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 10).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 10).build(),
                new ItemBuilder(Material.LEATHER_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 10).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return Collections.singletonList(
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)
        );
    }

    @Override
    public void onSelect(Player player) {
        // Nothing special
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;

        ProjectileSource shooter = arrow.getShooter();
        if (!(shooter instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null || profile.getProfileState() == ProfileState.SPAWN) {
            player.sendMessage(CC.translate("&cYou can't shoot arrows in spawn."));
            event.setCancelled(true);
            return;
        }

        // Only for Archer kit
        if (!profile.getCurrentKit().equalsIgnoreCase(getName())) return;

        shotLocations.put(player.getUniqueId(), player.getLocation());
    }

    @EventHandler
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        ProjectileSource shooterObj = arrow.getShooter();
        if (!(shooterObj instanceof Player shooter)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(shooter.getUniqueId());

        if (profile == null) return;
        if (!profile.getCurrentKit().equalsIgnoreCase(getName())) return;
        if (damaged.getUniqueId().equals(shooter.getUniqueId())) return;

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(damaged)) {
            event.setCancelled(true);
            shooter.sendMessage(CC.translate("&cYou can't damage players in spawn."));
            return;
        }

        Location start = shotLocations.remove(shooter.getUniqueId());
        if (start == null) return;

        int distance = (int) start.distance(damaged.getLocation());

        double damage;
        if (distance >= 30) damage = 4.0;
        else if (distance >= 25) damage = 3.5;
        else if (distance >= 20) damage = 3.0;
        else if (distance >= 10) damage = 2.5;
        else damage = 2.0;

        double newHp = damaged.getHealth() - damage;
        damaged.setHealth(Math.max(0, newHp));

        shooter.sendMessage(CC.translate("&c[&e" + distance + " Blocks&c] &fYou dealt &4" + damage + "❤ &fto &c" + damaged.getName() + "&f!"));
    }
}
