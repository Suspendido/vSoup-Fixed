package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class PhantomAbility implements KitAbility {

    private final Timer phantomTimer;

    public PhantomAbility() {
        this.phantomTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(30));
        SoupPvP.getInstance().getTimerManager().registerTimer(phantomTimer);
    }

    @Override
    public String getName() {
        return "Phantom";
    }

    @Override
    public String getDescription() {
        return "&fFly for 5 seconds with invisibility";
    }

    @Override
    public String getColor() {
        return "&7";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.FEATHER).name("&7Phantom").build();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item == null || !AbilityItemComparator.isSameAbilityItem(item, getItem())) return;

        Action act = event.getAction();
        if (act != Action.RIGHT_CLICK_AIR && act != Action.RIGHT_CLICK_BLOCK) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent()) return;
        if (!hasAbility(player, profile, getName())) return;

        event.setCancelled(true);

        if (profile.getProfileState() == ProfileState.SPAWN && SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.t("&cYou can't use this while in spawn."));
            return;
        }

        if (phantomTimer.hasTimer(player)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(phantomTimer.getRemaining(player), true) + "&c."));
            return;
        }

        phantomTimer.applyTimer(player);
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
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (!hasAbility(player, profile, getName())) return;

        if (profile.getCurrentKit() != null) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }
}
