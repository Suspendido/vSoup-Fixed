package kami.gg.souppvp.killstreak.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.Killstreak;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ThirtyKillstreak extends Killstreak implements Listener {

    private final SoupPvP plugin = SoupPvP.getInstance();

    @Getter
    private final Map<UUID, List<Wolf>> wolvesMap = new HashMap<>();

    @Override
    public String getName() {
        return "Attack Dogs";
    }

    @Override
    public int getRequired() {
        return 30;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.MONSTER_EGG)
                .durability(95)
                .name(CC.t("&a" + getName()))
                .lore(
                        CC.MENU_BAR,
                        "&7Spawns a squad of loyal wolves",
                        "&7that attack your enemies.",
                        CC.MENU_BAR,
                        "",
                        "&fKillstreak Required: &d" + getRequired()
                ).build();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        Profile profile = plugin.getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        int required = getRequiredKillstreak(profile);

        if (profile.getCurrentKillstreak() == required) {
            killer.sendMessage(CC.t("&aYou've received the &d" + getName() + " &aperk for reaching a &d" + required + " &akillstreak!"));
            spawnWolves(killer);
        }
    }

    private int getRequiredKillstreak(Profile profile) {
        Perk hardline = plugin.getPerksHandler().getPerkByName("Hardline");
        return (profile.getActivePerks().size() > 1 && plugin.getPerksHandler().getPerkByName(profile.getActivePerks().get(1)) == hardline) ? getRequired() - 1 : getRequired();
    }

    private void spawnWolves(Player owner) {
        List<Wolf> wolves = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Wolf wolf = owner.getWorld().spawn(owner.getLocation(), Wolf.class);

            wolf.setOwner(owner);
            wolf.setCustomName(CC.t(owner.getDisplayName() + "&c's Attack Dog"));
            wolf.setCustomNameVisible(false);

            wolf.setTamed(true);
            wolf.setAdult();
            wolf.setAgeLock(true);
            wolf.setAngry(true);

            wolf.setMaxHealth(200);
            wolf.setHealth(200);

            wolf.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
            wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

            wolves.add(wolf);
        }

        wolvesMap.put(owner.getUniqueId(), wolves);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Wolf wolf && event.getDamager() instanceof Player player) {
            if (wolf.isTamed() && player.equals(wolf.getOwner())) {
                player.sendMessage(ChatColor.RED + "You cannot damage your own Attack Dogs.");
                event.setCancelled(true);
            }
        }

        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Wolf wolf && wolf.getOwner() instanceof Player owner) {
            event.setCancelled(true);
            victim.damage(4.0, owner);
        }
    }

    @EventHandler
    public void onWolfTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Wolf wolf)) return;
        if (!(wolf.getOwner() instanceof Player owner)) return;

        if (event.getTarget() instanceof Player target) {
            if (target.getUniqueId().equals(owner.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onOwnerDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player owner)) return;

        List<Wolf> wolves = wolvesMap.get(owner.getUniqueId());
        if (wolves == null) return;

        Player attacker = null;

        if (event.getDamager() instanceof Player p) {
            attacker = p;
        } else if (event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) {
            attacker = p;
        }

        if (attacker == null) return;

        for (Wolf wolf : wolves) {
            if (!wolf.isValid()) continue;
            wolf.setTarget(attacker);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeWolves(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDeathRemove(PlayerDeathEvent event) {
        removeWolves(event.getEntity().getUniqueId());
    }

    private void removeWolves(UUID uuid) {
        List<Wolf> wolves = wolvesMap.remove(uuid);
        if (wolves == null) return;

        for (Wolf wolf : wolves) {
            if (wolf.isValid()) {
                wolf.remove();
            }
        }
    }
}
