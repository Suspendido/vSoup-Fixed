package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PalioxisAbility implements KitAbility {

    @Override
    public String getName() {
        return "Palioxis";
    }

    @Override
    public String getDescription() {
        return "&fGain ender pearl + Speed III on kill";
    }

    @Override
    public String getColor() {
        return "&5";
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.ENDER_PEARL);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        if (profile == null) return;
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (!hasAbility(killer, profile, getName())) return;

        PotionEffect previousSpeed = killer.getActivePotionEffects().stream()
                .filter(pe -> pe.getType() == PotionEffectType.SPEED)
                .findFirst()
                .orElse(null);

        int boostDuration = 20 * 10;
        boolean alreadySpeed3 = previousSpeed != null && previousSpeed.getAmplifier() == 2;
        int duration = alreadySpeed3 ? previousSpeed.getDuration() + boostDuration : boostDuration;

        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 2), true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!killer.isOnline()) return;
                if (profile.getProfileState() == ProfileState.SPAWN || profile.isInEvent()) return;

                killer.removePotionEffect(PotionEffectType.SPEED);

                if (previousSpeed != null && !alreadySpeed3) {
                    killer.addPotionEffect(new PotionEffect(
                            previousSpeed.getType(),
                            Integer.MAX_VALUE,
                            previousSpeed.getAmplifier()
                    ), true);
                }
            }
        }.runTaskLater(SoupPvP.getInstance(), duration);

        if (!killer.getInventory().contains(Material.ENDER_PEARL)) {
            if (killer.getInventory().firstEmpty() != -1) {
                killer.getInventory().addItem(getItem().clone());
            } else {
                for (int slot = 0; slot < killer.getInventory().getSize(); slot++) {
                    ItemStack item = killer.getInventory().getItem(slot);
                    if (item == null) continue;

                    Material type = item.getType();
                    if (type == Material.BOWL || type == Material.MUSHROOM_SOUP) {
                        killer.getInventory().setItem(slot, getItem().clone());
                        break;
                    }
                }
            }
        }

        killer.updateInventory();
    }
}
