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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class MelonAbility implements KitAbility {

    private final ItemStack melonTossItem = new ItemBuilder(Material.SPECKLED_MELON).name("&2Melon Toss").build();

    @Override
    public String getName() {
        return "Melon";
    }

    @Override
    public String getDescription() {
        return "&fToss melon to knock enemies into the air";
    }

    @Override
    public String getColor() {
        return "&2";
    }

    @Override
    public ItemStack getItem() {
        return melonTossItem.clone();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;

        ItemStack item = event.getItem();
        if (item == null || !item.isSimilar(melonTossItem)) return;

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        event.setCancelled(true);
        player.updateInventory();

        if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Melon Toss", true)) {
            player.sendMessage(CC.t("&cYou can't use this for another &e" + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Melon Toss", true), true) + "&c."));
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(BlockUtil.getTargetBlock(player, 20).getLocation())) {
            player.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        SoupPvP.getInstance().getTimersHandler().addPlayerTimer(player.getUniqueId(), new Timer("Melon Toss", TimeUnit.SECONDS.toMillis(30)), true);
        XPBarTimer.runXpBar(player, 30);
        PlayerUtil.playSound(player, Sound.EXPLODE, 1.0);
        
        FallingBlock block = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.MELON_BLOCK, (byte) 0);

        block.setDropItem(false);
        block.setMetadata("melon_tosser", new FixedMetadataValue(SoupPvP.getInstance(), player.getUniqueId()));

        block.setVelocity(player.getEyeLocation().getDirection().multiply(2.5).add(new Vector(0, 0.3, 0)));

        // search for players hit
        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.isDead() || !block.isValid() || !player.isOnline()) {
                    cancel();
                    return;
                }

                for (Entity entity : block.getNearbyEntities(3, 3, 3)) {
                    if (!(entity instanceof Player other)) continue;

                    if (other.getUniqueId().equals(player.getUniqueId())) continue;

                    Profile targetProfile = SoupPvP.getInstance().getProfilesHandler()
                            .getProfileByUUID(other.getUniqueId());

                    if (targetProfile.getProfileState() == ProfileState.SPAWN) continue;

                    block.remove();
                    cancel();

                    Vector velocity = other.getLocation().toVector()
                            .subtract(player.getLocation().toVector())
                            .multiply(0.3)
                            .setY(1.5);

                    other.setVelocity(velocity);
                    break;
                }
            }
        }.runTaskTimer(SoupPvP.getInstance(), 2L, 2L);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getEntity().hasMetadata("melon_tosser")) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }
}
