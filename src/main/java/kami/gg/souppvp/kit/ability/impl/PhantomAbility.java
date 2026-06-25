package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
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

    private static final ItemStack PHANTOM_FEATHER = new ItemBuilder(Material.FEATHER).name(CC.t("&7Phantom")).build();

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
        return PHANTOM_FEATHER.clone();
    }

    @Override
    public void onKitSelect(Player player) {
        // Nothing
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item == null || !item.isSimilar(PHANTOM_FEATHER)) return;

        Action act = event.getAction();
        if (act != Action.RIGHT_CLICK_AIR && act != Action.RIGHT_CLICK_BLOCK) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent()) return;

        event.setCancelled(true);

        if (profile.getProfileState() == ProfileState.SPAWN && SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.t("&cYou can't use this while in spawn."));
            return;
        }

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Phantom Flight", true)) {
            player.sendMessage(CC.t("&cYou can't use this for another " + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Phantom Flight", true), true) + "&c."));
            return;
        }

        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(player.getUniqueId(), new Timer("Phantom Flight", TimeUnit.SECONDS.toMillis(30)), true);
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

        if (profile.getCurrentKit() != null) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }
}
