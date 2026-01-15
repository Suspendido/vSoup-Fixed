package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.KillstreakReward;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SoupListeners implements Listener {

    private static final double SOUP_HEAL = 7.0;
    private static final double MAX_HEALTH = 20.0;
    private static final double CONSUME_THRESHOLD = 19.5;

    @EventHandler
    public void onSoupConsumption(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item == null || item.getType() == Material.AIR) return;
        if (player.getHealth() >= CONSUME_THRESHOLD) return;

        if (item.isSimilar(KillstreakReward.GRANDMA_SOUP)) {
            event.setCancelled(true);
            consumeSoup(player, MAX_HEALTH);
            return;
        }

        if (item.getType() == Material.MUSHROOM_SOUP) {
            event.setCancelled(true);
            consumeSoup(player, Math.min(player.getHealth() + SOUP_HEAL, MAX_HEALTH));
        }
    }

    private void consumeSoup(Player player, double newHealth) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        player.setHealth(newHealth);

        if (profile != null && profile.getEnableEasySoup()) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setType(Material.BOWL);
        }

        player.updateInventory();
    }


    @EventHandler
    public void onBowlDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.SURVIVAL) return;

        ItemStack dropped = event.getItemDrop().getItemStack();
        Material type = dropped.getType();

        if (type.name().contains("SWORD") || type.name().contains("AXE") || dropped.hasItemMeta()) {
            event.setCancelled(true);
            player.updateInventory();
            return;
        }

        if (type == Material.BOWL) {
            event.getItemDrop().remove();
        }
    }
}
