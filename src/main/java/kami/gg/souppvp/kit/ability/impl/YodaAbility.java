package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YodaAbility implements KitAbility {

    private final ItemStack theForce = new ItemBuilder(Material.INK_SACK).durability((short) 2).name("&aThe Force").build();

    @Override
    public String getName() {
        return "Yoda";
    }

    @Override
    public String getDescription() {
        return "&fPull nearby enemies towards you with The Force";
    }

    @Override
    public String getColor() {
        return "&a";
    }

    @Override
    public ItemStack getItem() {
        return theForce.clone();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (event.getPlayer().getItemInHand().isSimilar(theForce) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            event.setCancelled(true);
            player.updateInventory();

            if (profile.isInEvent() || SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
                player.sendMessage(CC.t("&cYou can't use this while in spawn."));
                return;
            }

            if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "The Force", true)) {
                player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "The Force", true), true) + "&c."));
                return;
            }

            SoupPvP.getInstance().getTimersHandler().addPlayerTimer(player.getUniqueId(), new Timer("The Force", TimeUnit.SECONDS.toMillis(45)), true);
            XPBarTimer.runXpBar(player, 45);
            PlayerUtil.playSound(player, Sound.ENDERMAN_STARE, 1.0);

            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    PlayerUtil.playSound((Player) entity, Sound.ENDERMAN_STARE, 1.0);
                    if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(entity)) return;
                    entity.sendMessage(CC.t("&cYou are being pulled by The Force."));
                }
            }

            Location location = player.getLocation();
            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    if (i >= 50) {
                        cancel();
                    }
                    ++i;
                    for (Player targets : Bukkit.getOnlinePlayers()) {
                        if (targets.getLocation().distance(location) <= 10 && targets != player) {
                            Profile targetsprofile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(targets.getUniqueId());
                            if (targetsprofile.getProfileState() == ProfileState.SPAWN) return;
                            moveToward(targets, location);
                        }
                    }
                }
            }.runTaskTimer(SoupPvP.getInstance(), 2L, 2L);
        }
    }

    private void moveToward(Entity entity, Location to) {
        Location loc = entity.getLocation();
        double x = loc.getX() - to.getX();
        double y = loc.getY() - to.getY();
        double z = loc.getZ() - to.getZ();
        Vector velocity = new Vector(x, y, z).normalize().multiply(-0.5);
        entity.setVelocity(velocity);
    }
}
