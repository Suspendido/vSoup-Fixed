package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.util.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.*;
import kami.gg.souppvp.util.projectile.event.CustomProjectileHitEvent;
import kami.gg.souppvp.util.projectile.projectile.ItemProjectile;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class NinjaAbility implements KitAbility {

    private final Timer shurikenTimer;

    public NinjaAbility() {
        this.shurikenTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(10));
        SoupPvP.getInstance().getTimerManager().registerTimer(shurikenTimer);
    }

    @Override
    public String getName() {
        return "Ninja";
    }

    @Override
    public String getDescription() {
        return "&fThrow shurikens that blind + slow enemies. +30 armor durability per kill";
    }

    @Override
    public String getColor() {
        return "&b";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.NETHER_STAR).name("&bShuriken").amount(4).build();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;

        // Check if player has Ninja ability
        if (!hasAbility(killer, profile, getName())) return;

        for (ItemStack armor : killer.getInventory().getArmorContents()) {
            if (armor != null) {
                armor.setDurability((short) Math.max(0, armor.getDurability() - 30));
            }
        }

        for (ItemStack content : killer.getInventory()) {
            if (content != null && content.getType() == Material.NETHER_STAR) {
                content.setAmount(4);
                break;
            }
        }

        killer.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "JUTSU! " + ChatColor.YELLOW + "You earned an extra shuriken star!");
        killer.updateInventory();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (item == null || !AbilityItemComparator.isSameAbilityItem(item, getItem())) return;
        if (!hasAbility(player, profile, "Ninja")) return;

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;

        if (shurikenTimer.hasTimer(player)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(shurikenTimer.getRemaining(player), true) + "&c."));
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player.getLocation())) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        shurikenTimer.applyTimer(player);
        XPBarTimer.runXpBar(player, 10);

        // Launch shuriken
        player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 1F, 1F);

        ItemProjectile projectile = new ItemProjectile("SHURIKEN", player, new ItemStack(Material.NETHER_STAR), 2);
        projectile.addTypedRunnable(o -> o.getEntity().getWorld().spigot().playEffect(o.getEntity().getLocation(), Effect.HAPPY_VILLAGER));

        // Remove 1 shuriken
        item.setAmount(item.getAmount() - 1);
        if (item.getAmount() <= 0) player.setItemInHand(null);

        player.updateInventory();
    }

    @EventHandler
    public void onHit(CustomProjectileHitEvent event) {
        if (!event.getProjectile().getProjectileName().equals("SHURIKEN")) return;
        if (event.getHitType() != CustomProjectileHitEvent.HitType.ENTITY) return;
        if (!(event.getHitEntity() instanceof Player target)) return;
        if (target == event.getProjectile().getShooter()) return;

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(target)) return;

        // Effects
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));

        target.damage(4.0);
        target.playSound(target.getLocation(), Sound.ANVIL_LAND, 1f, 1f);
    }
}
