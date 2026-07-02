package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.util.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BarbarianAbility implements KitAbility {

    private final Timer swarmTimer;

    public BarbarianAbility() {
        this.swarmTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(35));
        SoupPvP.getInstance().getTimerManager().registerTimer(swarmTimer);
    }

    @Override
    public String getName() {
        return "Barbarian";
    }

    @Override
    public String getDescription() {
        return "&fSpawn silverfish swarm to torture enemies";
    }

    @Override
    public String getColor() {
        return "&9";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.INK_SACK).name("&9Silverfish Swarm").durability(6).build();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;

        if (!hasAbility(player, profile, getName())) return;
        if (!AbilityItemComparator.isSameAbilityItem(item, getItem())) return;

        // Spawn check
        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.t("&cYou can't do this in Spawn."));
            return;
        }

        // Cooldown check
        if (swarmTimer.hasTimer(player)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(swarmTimer.getRemaining(player), true) + "&c."));
            return;
        }

        // Apply cooldown immediately
        swarmTimer.applyTimer(player);
        XPBarTimer.runXpBar(player, 35);

        // Slow effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 6, 0), true);

        // Find closest target
        Player closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (!(entity instanceof Player p)) continue;

            if (p.getUniqueId().equals(player.getUniqueId())) continue;

            Profile pProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(p.getUniqueId());
            if (pProfile.getProfileState() == ProfileState.SPAWN) continue;

            double distance = player.getLocation().distance(p.getLocation());
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = p;
            }
        }

        // Spawn silverfish swarm
        for (int i = 0; i < 4; i++) {
            Silverfish sf = (Silverfish) player.getWorld().spawnEntity(player.getLocation(), EntityType.SILVERFISH);

            sf.setMetadata("owner", new FixedMetadataValue(SoupPvP.getInstance(), player.getUniqueId()));
            sf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 3));

            if (closest != null) sf.setTarget(closest);

            // Remove after 10 seconds
            Bukkit.getScheduler().runTaskLater(SoupPvP.getInstance(), () -> {
                if (sf.isValid()) sf.remove();
            }, 20L * 10);
        }

        player.playSound(player.getLocation(), Sound.SLIME_WALK2, 1F, 1F);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (victim instanceof Silverfish sf && damager instanceof Player player) {
            if (sf.hasMetadata("owner") && UUID.fromString(sf.getMetadata("owner").getFirst().asString()).equals(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(CC.t("&cYou cannot damage your own silverfish."));
            }
            return;
        }

        if (victim instanceof Player damaged && damager instanceof Silverfish sf) {
            event.setCancelled(true);
            if (sf.hasMetadata("owner")) {
                Player owner = Bukkit.getPlayer(UUID.fromString(sf.getMetadata("owner").getFirst().asString()));
                if (owner != null) damaged.damage(4, owner);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0);
    }
}
