package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class SwitcherooAbility implements KitAbility {

    private final ItemStack switcherooItem = new ItemBuilder(Material.SNOW_BALL).name("&9Switcheroo").amount(3).build();

    @Override
    public String getName() {
        return "Switcheroo";
    }

    @Override
    public String getDescription() {
        return "&fThrow snowball to swap locations with enemy";
    }

    @Override
    public String getColor() {
        return "&9";
    }

    @Override
    public ItemStack getItem() {
        return switcherooItem.clone();
    }

    @EventHandler
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent() || SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) return;

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getPlayer().getItemInHand().isSimilar(switcherooItem)) {
                if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Switcheroo", true)) {
                    player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Switcheroo", true), true) + "&c."));
                    event.setCancelled(true);
                    event.setUseItemInHand(Event.Result.DENY);
                    player.updateInventory();
                } else {
                    // Apply cooldown when throwing the snowball
                    SoupPvP.getInstance().getTimersHandler().addPlayerTimer(player.getUniqueId(), new Timer("Switcheroo", TimeUnit.SECONDS.toMillis(45)), true);
                    XPBarTimer.runXpBar(player, 45);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player damaged && event.getDamager() instanceof Snowball) {
            Entity damager = event.getDamager();
            Snowball snowball = (Snowball) damager;
            if (!(snowball.getShooter() instanceof Player shooter)) return;

            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(shooter.getUniqueId());
            Profile damagedprofile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(damaged.getUniqueId());

            if (!profile.isInEvent()) {
                if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(damaged)) {
                    shooter.sendMessage(CC.t("&cYou cannot switch players in spawn."));
                    // Refund cooldown if switch fails
                    SoupPvP.getInstance().getTimersHandler().removePlayerTimer(shooter.getUniqueId(), true);
                    return;
                }

                Location location = damaged.getLocation();
                damaged.teleport(shooter);
                shooter.teleport(location);
                PlayerUtil.playSound(shooter, Sound.CHICKEN_EGG_POP, 1.0);
                PlayerUtil.playSound(damaged, Sound.CHICKEN_EGG_POP, 1.0);
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());

        if (!profile.isInEvent()) {
            for (ItemStack itemStack : killer.getInventory().getContents()) {
                if (itemStack.isSimilar(switcherooItem)) {
                    if (itemStack.getAmount() == 3) return;
                } else {
                    killer.getInventory().setItem(1, switcherooItem.clone());
                }
            }
        }
    }
}
