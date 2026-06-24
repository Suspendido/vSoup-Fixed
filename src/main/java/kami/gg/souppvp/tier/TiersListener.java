package kami.gg.souppvp.tier;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class TiersListener implements Listener {

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
        int experiences = profile.getExperiences();
        int currentTier = profile.getTier();

        // Calcular el XP requerido para el siguiente tier
        int requiredXP = TierUtils.calculateNextTierXP(currentTier);

        // Verificar si el jugador tiene suficiente XP para subir de tier
        if (experiences >= requiredXP) {
            int newTier = currentTier + 1;
            TierCategory currentCategory = TierCategory.getCategoryByLevel(currentTier);
            TierCategory newCategory = TierCategory.getCategoryByLevel(newTier);

            // Actualizar tier
            profile.setTier(newTier);

            // Dar reward de credits
            int reward = TierUtils.calculateTierReward(newTier);
            profile.setCredits(profile.getCredits() + reward);

            List<String> message = new ArrayList<>();
            message.add(CC.MENU_BAR);
            message.add("&b&lLevel Up!");
            message.add(currentCategory.getColor() + "[" + currentTier + currentCategory.getFormattedIcon() + "] &7➤ " + newCategory.getColor() + "[" + newTier + newCategory.getFormattedIcon() + "]");
            message.add("");
            message.add("&b&lRewards");
            message.add("&7➜ " + reward + " Credits");

            // Verificar si desbloqueó una nueva categoría de iconos
            if (TierUtils.isNewCategory(currentTier, newTier)) {
                message.add("&7➜ " + newCategory.getColor() + newCategory.getName() + " Icon Unlocked");
            }

            message.add(CC.MENU_BAR);

            for (String s : message) {
                killer.sendMessage(CC.t(s));
            }

            PlayerUtil.playSound(killer, Sound.LEVEL_UP, 0.7);
            profile.saveProfile();
        }
    }
}
