package kami.gg.souppvp.util;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class SpectatorUtil {

    public static void resetPlayer(Player player) {

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        profile.setProfileState(ProfileState.SPECTATING_EVENT);

        player.setHealth(20);
        player.resetMaxHealth();
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        player.getInventory().setItem(0, SpawnItems.KITS_SELECTOR);
        player.getInventory().setItem(1, SpawnItems.HOST_EVENTS);
        player.getInventory().setItem(3, SpawnItems.SHOP);
        player.getInventory().setItem(5, SpawnItems.PERK_SELECTOR);
        player.getInventory().setItem(7, SpawnItems.PREVIOUS_KIT);
        player.getInventory().setItem(8, SpawnItems.YOUR_OPTIONS);

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
        SoupPvP.getInstance().getTimersHandler().removeAllPlayerTimers(player.getUniqueId());
        SoupPvP.getInstance().getCombatTagsHandler().getCombatTags().remove(player.getUniqueId());
        SoupPvP.getInstance().getNoFallDamageHandler().getNoFallDamage().remove(player.getUniqueId());
        SoupPvP.getInstance().getSpawnTeleportationHandler().getSpawnTeleporataion().remove(player.getUniqueId());

    }

}
