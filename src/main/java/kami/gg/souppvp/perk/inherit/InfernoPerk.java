package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.TaskUtil;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InfernoPerk extends Perk implements Listener {

    @Override
    public String getName() {
        return "Inferno";
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7After being on fire for 10 seconds", "&7You will be extinguished");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.FIREBALL);
    }

    @Override
    public int getCost() {
        return 850;
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent()) return;
        if (!profile.getActivePerks().contains(getName())) return;

        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.FIRE_TICK && cause != EntityDamageEvent.DamageCause.FIRE) return;

        TaskUtil.runLater(() -> {
            if (!player.isOnline() || player.getFireTicks() <= 0) return;

            player.setFireTicks(0);
            player.playSound(player.getLocation(), Sound.WITHER_SHOOT, 1F, 1F);
            player.getWorld().spigot().playEffect(
                    player.getLocation().add(0, 1.5, 0),
                    Effect.LARGE_SMOKE, 26, 0,
                    0.1F, 0.5F, 0.1F, 0.2F, 50, 50
            );
        }, 20 * 10);
    }

}
