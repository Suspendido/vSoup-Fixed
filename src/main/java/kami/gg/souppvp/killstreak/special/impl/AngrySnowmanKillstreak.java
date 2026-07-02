package kami.gg.souppvp.killstreak.special.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialTypeKillstreak;
import kami.gg.souppvp.util.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.UUID;

public class AngrySnowmanKillstreak implements SpecialKillstreak, Listener {

    @Getter
    private final HashMap<UUID, Snowman> snowmen = new HashMap<>();

    @Override
    public SpecialTypeKillstreak getSpecialType() {
        return SpecialTypeKillstreak.ANGRY_SNOWMAN;
    }

    @Override
    public void apply(Player player, ConfigurableKillstreak.RewardData rewardData) {
        spawnSnowman(player);
    }

    private void spawnSnowman(Player owner) {
        Snowman snowman = (Snowman) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.SNOWMAN);

        snowman.setMetadata("owner", new FixedMetadataValue(SoupPvP.getInstance(), owner.getUniqueId().toString()));

        snowman.setCustomName(CC.t("&b&l" + owner.getName() + "'s Angry Snowman"));
        snowman.setMaxHealth(500);
        snowman.setHealth(500);

        Player closest = getClosestPlayer(owner);
        if (closest != null) {
            snowman.setTarget(closest);
        }

        snowmen.put(owner.getUniqueId(), snowman);
        Bukkit.getScheduler().runTaskLater(SoupPvP.getInstance(), () -> removeSnowman(owner.getUniqueId()), 20L * 60 * 5);
    }

    private Player getClosestPlayer(Player owner) {
        Player closest = null;
        double distance = Double.MAX_VALUE;

        for (Entity entity : owner.getNearbyEntities(10, 10, 10)) {
            if (!(entity instanceof Player target)) continue;
            if (target.getUniqueId().equals(owner.getUniqueId())) continue;

            double d = owner.getLocation().distanceSquared(target.getLocation());
            if (d < distance) {
                distance = d;
                closest = target;
            }
        }
        return closest;
    }

    private void removeSnowman(UUID uuid) {
        Snowman snowman = snowmen.remove(uuid);
        if (snowman != null && snowman.isValid()) {
            snowman.remove();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Snowman snowman && event.getDamager() instanceof Player player) {
            if (isOwner(snowman, player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot damage your own Angry Snowman.");
                return;
            }
        }

        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Snowman snowman) {
            event.setCancelled(true);
            Player owner = getOwner(snowman);

            if (owner != null) {
                event.setCancelled(true);
                victim.damage(4, owner);
            }
        }
    }

    @EventHandler
    public void onSnowballDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Snowman snowman)) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        Player owner = getOwner(snowman);
        if (owner == null) return;

        event.setCancelled(true);
        victim.damage(4, owner);
    }

    @EventHandler
    public void onSnowmanMove(PlayerMoveEvent event) {
        Snowman snowman = snowmen.get(event.getPlayer().getUniqueId());
        if (snowman == null || !snowman.isValid()) return;

        if (event.getPlayer().getLocation().distanceSquared(snowman.getLocation()) > 225) {
            snowman.teleport(event.getPlayer());
        }

        removeSnowLayer(snowman.getLocation().getBlock());
        removeSnowLayer(snowman.getLocation().clone().add(0, -1, 0).getBlock());
    }

    @EventHandler
    public void onSnowmanTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Snowman snowman)) return;

        if (!(event.getTarget() instanceof Player target)) {
            event.setCancelled(true);
            return;
        }

        if (isOwner(snowman, target)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeSnowman(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDeathRemove(PlayerDeathEvent event) {
        removeSnowman(event.getEntity().getUniqueId());
    }

    private void removeSnowLayer(Block block) {
        if (block.getType() == Material.SNOW) {
            block.setType(Material.AIR);
        }
    }

    private boolean isOwner(Snowman snowman, Player player) {
        return snowman.hasMetadata("owner") && UUID.fromString(snowman.getMetadata("owner").getFirst().asString()).equals(player.getUniqueId());
    }

    private Player getOwner(Snowman snowman) {
        if (!snowman.hasMetadata("owner")) return null;
        return Bukkit.getPlayer(UUID.fromString(snowman.getMetadata("owner").getFirst().asString()));
    }
}
