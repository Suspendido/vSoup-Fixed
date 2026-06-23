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

        Tiers currentTier = profile.getTier();
        Tiers nextTier = currentTier.getNext();

        if (nextTier != null && experiences >= nextTier.getRequiredExperiences()) {
            TierCategory currentCategory = TierCategory.getCategoryByLevel(currentTier.getTierLevel());
            profile.setTier(nextTier);
            TierCategory category = TierCategory.getCategoryByLevel(nextTier.getTierLevel());

            // Give credits reward
            int reward = nextTier.getCreditsReward();
            profile.setCredits(profile.getCredits() + reward);

            List<String> message = new ArrayList<>();
            message.add("&8" + CC.MENU_BAR);
            message.add("&b&lLevel Up!");
            message.add(currentCategory.getColor() + "[" + currentTier.getTierLevel() + currentCategory.getFormattedIcon() + "] &7➤ " + category.getColor() + "[" + nextTier.getTierLevel() + category.getFormattedIcon() + "]");
            message.add("");
            message.add("&b&lRewards");
            message.add("&7➜ " + reward + " Credits");

            // Check if new icon was unlocked
            if (!category.equals(currentCategory)) {
                message.add("&7➜ " + category.getColor() + category.getName());
            }

            message.add("&8" + CC.MENU_BAR);

            for (String s : message) {
                killer.sendMessage(CC.t(s));
            }

            PlayerUtil.playSound(killer, Sound.LEVEL_UP, 0.7);
            profile.saveProfile();
        }
    }
}
