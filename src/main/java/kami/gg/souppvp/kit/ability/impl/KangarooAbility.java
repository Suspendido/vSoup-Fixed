package kami.gg.souppvp.kit.ability.impl;

import com.google.common.collect.Sets;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.AbilityItemComparator;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class KangarooAbility implements KitAbility {

    private final Set<UUID> jumpingUsers = Sets.newHashSet();
    private final Timer kangarooTimer;

    public KangarooAbility() {
        this.kangarooTimer = new Timer(getName(), TimeUnit.SECONDS.toMillis(10));
        SoupPvP.getInstance().getTimerManager().registerTimer(kangarooTimer);
    }

    @Override
    public String getName() {
        return "Kangaroo";
    }

    @Override
    public String getDescription() {
        return "&fJump boost ability with no fall damage";
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.FIREWORK).name("&cKangaroo Boost").build();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getTo().getBlockX() == event.getFrom().getBlockX() && 
            event.getTo().getBlockY() == event.getFrom().getBlockY() && 
            event.getTo().getBlockZ() == event.getFrom().getBlockZ()) {
            return;
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;

        if (player.isOnGround()) {
            jumpingUsers.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (!hasAbility(player, profile, getName())) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && jumpingUsers.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile == null || profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!hasAbility(player, profile, getName())) return;

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || !AbilityItemComparator.isSameAbilityItem(item, getItem())) return;

        event.setCancelled(true);

        UUID uuid = player.getUniqueId();

        if (kangarooTimer.hasTimer(player)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(kangarooTimer.getRemaining(player), true) + "&c."));
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player.getLocation())) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        kangarooTimer.applyTimer(player);
        XPBarTimer.runXpBar(player, 10);

        player.setVelocity(player.getEyeLocation().getDirection().multiply(1.5).setY(1.25));

        jumpingUsers.add(uuid);
        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1F, 1F);
    }
}
