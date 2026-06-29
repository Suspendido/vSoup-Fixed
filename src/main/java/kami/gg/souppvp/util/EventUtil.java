package kami.gg.souppvp.util;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class EventUtil {

    public static void resetPlayer(Player player) {

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        profile.setProfileState(ProfileState.IN_EVENT);

        player.setHealth(20);
        player.resetMaxHealth();
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.1F);

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        player.getInventory().setItem(8, EventItems.LEAVE_EVENT);

        player.updateInventory();
        player.setLevel(0);
        player.setExp(0);
        player.setTotalExperience(0);
        player.setFireTicks(0);

        if (player.hasMetadata("noFall")) {
            player.removeMetadata("noFall", SoupPvP.getInstance());
        }

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        XPBarTimer.remove(player);
        SoupPvP.getInstance().getTimerManager().getTimer("Combat").removeTimer(player);
        SoupPvP.getInstance().getNoFallDamageHandler().getNoFallDamage().remove(player.getUniqueId());

    }

}
