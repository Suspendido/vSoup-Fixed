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
import kami.gg.souppvp.util.projectile.event.CustomProjectileHitEvent;
import kami.gg.souppvp.util.projectile.projectile.ItemProjectile;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class TorchAbility implements KitAbility {

    private final ItemStack dragonBreath = new ItemBuilder(Material.BLAZE_POWDER).name("&cDragon Breath").build();

    @Override
    public String getName() {
        return "Torch";
    }

    @Override
    public String getDescription() {
        return "&fShoot dragon breath to set enemies on fire";
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public ItemStack getItem() {
        return dragonBreath.clone();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (event.getPlayer().getItemInHand().isSimilar(dragonBreath) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            event.setCancelled(true);
            player.updateInventory();

            if (profile.isInEvent() || SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
                player.sendMessage(CC.t("&cYou can't use this while in spawn."));
                return;
            }

            if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Dragon Breath", true)) {
                player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Dragon Breath", true), true) + "&c."));
                return;
            }

            SoupPvP.getInstance().getTimersHandler().addPlayerTimer(player.getUniqueId(), new Timer("Dragon Breath", TimeUnit.SECONDS.toMillis(45)), true);
            XPBarTimer.runXpBar(player, 45);
            PlayerUtil.playSound(player, Sound.ENDERDRAGON_GROWL, 1.0);

            for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                if (entity instanceof Player) {
                    PlayerUtil.playSound((Player) entity, Sound.ENDERDRAGON_GROWL, 1.0);
                }
            }

            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    if (i >= 20) {
                        cancel();
                    }
                    ++i;
                    new ItemProjectile("DRAGON_BREATH", player, new ItemStack(Material.BLAZE_POWDER), 0.5f);
                }
            }.runTaskTimer(SoupPvP.getInstance(), 2L, 2L);
        }
    }

    @EventHandler
    public void onHit(CustomProjectileHitEvent event) {
        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(event.getHitEntity())) return;
        if (event.getProjectile().getProjectileName().equals("DRAGON_BREATH") && event.getHitEntity() instanceof Player && event.getHitEntity() != event.getProjectile().getShooter()) {
            event.getHitEntity().setFireTicks(40);
        }
    }
}
