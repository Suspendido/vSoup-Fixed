package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.TasksUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;

public class ShopItemsListener implements Listener {

    private static final Material MILK = Material.MILK_BUCKET;

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material consumedType = event.getItem().getType();

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            if (consumedType == MILK) {
                event.setCancelled(true);
            }
            return;
        }

        TasksUtility.runTaskLater(() -> {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            if (profile == null) return;

            Kit kit = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());
            if (kit == null) return;

            for (PotionEffect effect : kit.getPotionEffects()) {
                player.addPotionEffect(effect);
            }
        }, 2L);
    }
}
