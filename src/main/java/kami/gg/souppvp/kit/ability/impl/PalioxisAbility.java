package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PalioxisAbility implements KitAbility {

    private final ItemStack ENDER_PEARL = new ItemStack(Material.ENDER_PEARL);

    @Override
    public String getName() {
        return "Palioxis";
    }

    @Override
    public String getDescription() {
        return "&fGain ender pearl + Speed IV on kill";
    }

    @Override
    public String getColor() {
        return "&5";
    }

    @Override
    public ItemStack getItem() {
        return ENDER_PEARL.clone();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());

        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (killer.getInventory().contains(Material.ENDER_PEARL)) return;

        if (killer.getInventory().firstEmpty() != -1) {
            killer.getInventory().addItem(ENDER_PEARL.clone());
            return;
        }

        for (int slot = 0; slot < killer.getInventory().getSize(); slot++) {
            ItemStack item = killer.getInventory().getItem(slot);
            if (item == null) continue;

            Material type = item.getType();
            if (type == Material.BOWL || type == Material.MUSHROOM_SOUP || type == Material.ENDER_PEARL) {
                killer.getInventory().setItem(slot, ENDER_PEARL.clone());
                return;
            }
        }

        for (ItemStack armor : killer.getInventory().getArmorContents()) {
            if (armor != null) {
                armor.setDurability((short) Math.max(0, armor.getDurability() - 10));
            }
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(killer.getLocation())) {
            killer.sendMessage(CC.t("&cYou can't do this in spawn."));
            return;
        }

        int boostDuration = 20 * 10;
        PotionEffect currentSpeed3 = killer.getActivePotionEffects().stream()
                .filter(pe -> pe.getType() == PotionEffectType.SPEED && pe.getAmplifier() == 2)
                .findFirst()
                .orElse(null);
        int duration = currentSpeed3 != null ? currentSpeed3.getDuration() + boostDuration : boostDuration;
        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 3), true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!killer.isOnline()) return;
                if (profile.getProfileState() == ProfileState.SPAWN || profile.isInEvent()) return;

                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3), true);
            }
        }.runTaskLater(SoupPvP.getInstance(), duration);

        killer.updateInventory();
    }
}
