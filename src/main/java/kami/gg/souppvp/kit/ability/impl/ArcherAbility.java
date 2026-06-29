package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
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
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArcherAbility implements KitAbility {

    private final Map<UUID, Location> shotLocations = new HashMap<>();

    @Override
    public String getName() {
        return "Archer";
    }

    @Override
    public String getDescription() {
        return "&fLonger distance = more damage with arrows";
    }

    @Override
    public String getColor() {
        return "&5";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.BOW)
                .enchantment(Enchantment.DURABILITY, 3)
                .enchantment(Enchantment.ARROW_INFINITE, 1)
                .build();
    }

    @Override
    public void onKitSelect(Player player) {
        shotLocations.remove(player.getUniqueId());
    }

    @Override
    public void onKitDeselect(Player player) {
        shotLocations.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;

        ProjectileSource shooter = arrow.getShooter();
        if (!(shooter instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null || profile.getProfileState() == ProfileState.SPAWN) {
            player.sendMessage(CC.t("&cYou can't shoot arrows in spawn."));
            event.setCancelled(true);
            return;
        }

        if (!hasAbility(player, profile, getName())) return;

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
        if (damaged.getUniqueId().equals(shooter.getUniqueId())) return;

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(damaged)) {
            event.setCancelled(true);
            shooter.sendMessage(CC.t("&cYou can't damage players in spawn."));
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

        shooter.sendMessage(CC.t("&c[&e" + distance + " Blocks&c] &fYou dealt &4" + damage + "❤ &fto &c" + damaged.getName() + "&f!"));
    }
}
