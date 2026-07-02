package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.util.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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

    private final Timer switcherooTimer;

    public SwitcherooAbility() {
        this.switcherooTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(45));
        SoupPvP.getInstance().getTimerManager().registerTimer(switcherooTimer);
    }

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
        return new ItemBuilder(Material.SNOW_BALL).name("&9Switcheroo").amount(3).build();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent() || SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) return;
        if (!hasAbility(player, profile, getName())) return;

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (!AbilityItemComparator.isSameAbilityItem(player.getItemInHand(), getItem())) return;

        if (switcherooTimer.hasTimer(player)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + switcherooTimer.getRemainingString(player) + "&c."));
            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (!(event.getDamager() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Player shooter)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(shooter.getUniqueId());
        if (profile == null) return;
        if (!hasAbility(shooter, profile, getName())) return;
        if (profile.isInEvent()) return;

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(damaged)) {
            shooter.sendMessage(CC.t("&cYou cannot switch players in spawn."));
            return;
        }

        Location location = damaged.getLocation();
        damaged.teleport(shooter);
        shooter.teleport(location);

        switcherooTimer.applyTimer(shooter);
        XPBarTimer.runXpBar(shooter, 45);

        PlayerUtil.playSound(shooter, Sound.CHICKEN_EGG_POP, 1.0);
        PlayerUtil.playSound(damaged, Sound.CHICKEN_EGG_POP, 1.0);
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        if (profile == null) return;
        if (!hasAbility(killer, profile, getName())) return;
        if (profile.isInEvent()) return;

        for (ItemStack item : killer.getInventory().getContents()) {
            if (item != null && AbilityItemComparator.isSameAbilityItem(item, getItem()) && item.getAmount() >= 3) return;
        }

        killer.getInventory().setItem(1, getItem().clone());
    }
}
