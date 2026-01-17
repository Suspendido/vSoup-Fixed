package kami.gg.souppvp.killstreak.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.Killstreak;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.UUID;

public class FortyFiveKillstreak extends Killstreak implements Listener {

    @Getter
    private final HashMap<UUID, Snowman> snowmen = new HashMap<>();

    @Override
    public String getName() {
        return "Angry Snowman";
    }

    @Override
    public int getRequired() {
        return 45;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.SNOW_BALL)
                .name(CC.translate("&a" + getName()))
                .lore(
                        CC.MENU_BAR,
                        "&fSpawns an Angry Snowman",
                        "&fthat shoots strong snowballs",
                        "&ffor 5 minutes.",
                        CC.MENU_BAR,
                        "",
                        "&fKillstreak Required: &d" + getRequired()
                ).build();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        int required = getRequiredKillstreak(profile);

        if (profile.getCurrentKillstreak() == required) {
            killer.sendMessage(CC.translate("&aYou've received the &d" + getName() + " &aperk for reaching a &d" + required + " &akillstreak!"));
            spawnSnowman(killer);
        }
    }

    private int getRequiredKillstreak(Profile profile) {
        Perk hardline = SoupPvP.getInstance().getPerksHandler().getPerkByName("Hardline");
        return (profile.getActivePerks().size() > 1 && SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(1)) == hardline) ? getRequired() - 1 : getRequired();
    }

    private void spawnSnowman(Player owner) {
        Snowman snowman = (Snowman) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.SNOWMAN);

        snowman.setMetadata("owner", new FixedMetadataValue(SoupPvP.getInstance(), owner.getUniqueId().toString()));

        snowman.setCustomName(CC.translate("&b&l" + owner.getName() + "'s Angry Snowman"));
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
