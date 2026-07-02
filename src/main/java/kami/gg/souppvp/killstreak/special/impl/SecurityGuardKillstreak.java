package kami.gg.souppvp.killstreak.special.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialTypeKillstreak;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class SecurityGuardKillstreak implements SpecialKillstreak, Listener {

    private final SoupPvP plugin = SoupPvP.getInstance();
    @Getter private final HashMap<UUID, Zombie> guards = new HashMap<>();

    @Override
    public SpecialTypeKillstreak getSpecialType() {
        return SpecialTypeKillstreak.SECURITY_GUARD;
    }

    @Override
    public void apply(Player player, ConfigurableKillstreak.RewardData rewardData) {
        spawnGuard(player);
    }

    private void spawnGuard(Player owner) {
        Zombie zombie = (Zombie) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.ZOMBIE);

        zombie.setMetadata("owner", new FixedMetadataValue(plugin, owner.getUniqueId().toString()));
        zombie.setCustomName(CC.t("&b&l" + owner.getName() + "'s Security Guard"));
        zombie.setMaxHealth(1000);
        zombie.setHealth(1000);

        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));

        equipGuard(zombie);
        guards.put(owner.getUniqueId(), zombie);
    }

    private void equipGuard(Zombie zombie) {
        EntityEquipment eq = zombie.getEquipment();
        if (eq == null) return;

        eq.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.BLUE).build());
        eq.setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).color(Color.BLUE).build());
        eq.setBoots(new ItemBuilder(Material.LEATHER_BOOTS).color(Color.BLUE).build());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Zombie zombie && event.getDamager() instanceof Player player && isOwner(zombie, player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot damage your own Security Guard.");
            return;
        }

        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Zombie zombie) {
            Player owner = getOwner(zombie);
            if (owner != null) {
                event.setCancelled(true);
                victim.damage(4, owner);
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;
        if (!(event.getTarget() instanceof Player target)) return;

        if (isOwner(zombie, target)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Zombie zombie = guards.get(event.getPlayer().getUniqueId());
        if (zombie == null || !zombie.isValid()) return;

        if (event.getPlayer().getLocation().distanceSquared(zombie.getLocation()) > 225) {
            zombie.teleport(event.getPlayer());
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) return;

        if (zombie.hasMetadata("owner")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeGuard(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDeathRemove(PlayerDeathEvent event) {
        removeGuard(event.getEntity().getUniqueId());
    }

    private void removeGuard(UUID uuid) {
        Zombie zombie = guards.remove(uuid);
        if (zombie != null && zombie.isValid()) {
            zombie.remove();
        }
    }

    private boolean isOwner(Zombie zombie, Player player) {
        return zombie.hasMetadata("owner") && UUID.fromString(zombie.getMetadata("owner").getFirst().asString()).equals(player.getUniqueId());
    }

    private Player getOwner(Zombie zombie) {
        if (!zombie.hasMetadata("owner")) return null;
        return Bukkit.getPlayer(UUID.fromString(zombie.getMetadata("owner").getFirst().asString()));
    }
}
