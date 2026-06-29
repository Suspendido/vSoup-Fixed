package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.*;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

@Setter
public class ZeusAbility implements KitAbility {

    private final Timer zeusTimer;

    public ZeusAbility() {
        this.zeusTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(45));
        SoupPvP.getInstance().getTimerManager().registerTimer(zeusTimer);
    }

    @Override
    public String getName() {
        return "Zeus";
    }

    @Override
    public String getDescription() {
        return "&fStrike lightning on nearby players within 10 blocks";
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.GLOWSTONE_DUST).name("&6Lightning Bolt").build();
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        // Check if player has Zeus ability
        if (!hasAbility(player, profile, getName())) return;
        if (!AbilityItemComparator.isSameAbilityItem(player.getItemInHand(), getItem())) return;

        event.setCancelled(true);
        player.updateInventory();

        if (zeusTimer.hasTimer(player)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(zeusTimer.getRemaining(player), true) + "&c."));
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        zeusTimer.applyTimer(player);
        XPBarTimer.runXpBar(player, 45);

        boolean hit = false;
        for (Player target : player.getWorld().getPlayers()) {
            if (target == player) continue;
            Profile tProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());

            if (tProfile.getProfileState() == ProfileState.SPAWN) continue;
            if (target.getLocation().distance(player.getLocation()) > 10) continue;

            hit = true;

            target.damage(8, player);
            player.getWorld().strikeLightningEffect(target.getLocation());
        }

        if (!hit) {
            player.sendMessage(CC.t("&cNo players nearby."));
            zeusTimer.removeTimer(player);
            return;
        }

        PlayerUtil.playSound(player, Sound.AMBIENCE_THUNDER, 1.0);
    }
}