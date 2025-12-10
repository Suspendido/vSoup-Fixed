package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.menu.HostEventsMenu;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.menu.KitsSelectMenu;
import kami.gg.souppvp.options.OptionsMenu;
import kami.gg.souppvp.perk.menu.PerksMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.EventItems;
import kami.gg.souppvp.util.SpawnItems;
import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.DecimalFormat;

public class SpawnEventItemsListener implements Listener {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)){
            if (event.getItem().isSimilar(SpawnItems.KITS_SELECTOR)){
                new KitsSelectMenu().openMenu(player);
                return;
            }
            if (event.getItem().isSimilar(SpawnItems.HOST_EVENTS)){
                new HostEventsMenu().openMenu(player);
                return;
            }
            if (event.getItem().isSimilar(SpawnItems.GAME_PERKS)){
                new PerksMenu().openMenu(player);
                return;
            }
            if (event.getItem().isSimilar(SpawnItems.YOUR_STATISTICS)){
                player.sendMessage(CC.translate(StringUtils.repeat("&7&m-", 53)));
                player.sendMessage(CC.translate("&bYour Statistics:"));
                player.sendMessage(CC.translate(" &fKills: &b" + profile.getKills()));
                player.sendMessage(CC.translate(" &fDeaths: &b" + profile.getDeaths()));
                if (profile.getDeaths() == 0){
                    player.sendMessage(CC.translate(" &fKDR: &6Infinity"));
                } else {
                    double kdr = (double) profile.getKills() / (double) profile.getDeaths();
                    String context = kdr >= 1 ? "&a" : "&c";
                    player.sendMessage(CC.translate(" &fKDR: " + context + df.format(kdr)));
                }
                player.sendMessage(CC.translate(" &fCurrent Killstreak: &b" + profile.getCurrentKillstreak()));
                player.sendMessage(CC.translate(" &fHighest Killstreak: &b" + profile.getHighestKillstreak()));
                player.sendMessage(CC.translate(" &fCredits: &b" + profile.getCredits()));
                player.sendMessage(CC.translate(" &fTier: &7" + profile.getTier().getDisplay() + "✫"));
                if (profile.getBounty() > 0){
                    player.sendMessage(CC.translate(" &fBounty: &b" + profile.getBounty()));
                }
                player.sendMessage(CC.translate(StringUtils.repeat("&7&m-", 53)));
                return;
            }
            if (event.getItem().isSimilar(SpawnItems.PREVIOUS_KIT)) {
                if (profile.getPreviousKit() == null){
                    player.sendMessage(CC.translate("&cYou don't have a previous kit."));
                    return;
                }
                Kit current = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());
                Kit previous = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getPreviousKit());

                profile.setCurrentKit(previous.getName());
                profile.setPreviousKit(current.getName());

                player.sendMessage(CC.translate("&aSuccessfully given you your previous kit &r" + previous.getRarityType().getColor() + profile.getPreviousKit() + "&a"));
                return;
            }
            if (event.getItem().isSimilar(SpawnItems.YOUR_OPTIONS)){
                new OptionsMenu().openMenu(player);
                return;
            }
            if (event.getItem().isSimilar(EventItems.LEAVE_EVENT)){
                if (profile.getSumoEvent() != null){
                    profile.getSumoEvent().handleLeave(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event){
        if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
            Player player = event.getPlayer();
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event) {
        if (event.getWhoClicked().getGameMode().equals(GameMode.SURVIVAL)){
            Player player = (Player) event.getWhoClicked();
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)){
                event.setCancelled(true);
            }
        }
    }
}