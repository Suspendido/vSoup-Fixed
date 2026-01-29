package kami.gg.souppvp.listener.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.menu.HostEventsMenu;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitsHandler;
import kami.gg.souppvp.kit.menu.KitsSelectMenu;
import kami.gg.souppvp.options.OptionsMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.shop.ShopMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.EventItems;
import kami.gg.souppvp.util.SpawnItems;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class SpawnEventItemsListener implements Listener {

    private static final DecimalFormat KDR_FORMAT = new DecimalFormat("0.00");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (item == null || !isRightClick(event.getAction())) return;
        if (profile == null) return;

        if (item.isSimilar(SpawnItems.KITS_SELECTOR)) {
            new KitsSelectMenu().openMenu(player);
            return;
        }

        if (item.isSimilar(SpawnItems.HOST_EVENTS)) {
            new HostEventsMenu().openMenu(player);
            return;
        }

        if (item.isSimilar(SpawnItems.SHOP)) {
            new ShopMenu().openMenu(player);
            return;
        }

        if (item.isSimilar(SpawnItems.YOUR_STATISTICS)) {
            displayStatistics(player, profile);
            return;
        }

        if (item.isSimilar(SpawnItems.PREVIOUS_KIT)) {
            handlePreviousKit(player, profile);
            return;
        }

        if (item.isSimilar(SpawnItems.YOUR_OPTIONS)) {
            new OptionsMenu().openMenu(player);
            return;
        }

        if (item.isSimilar(EventItems.LEAVE_EVENT)) {
            if (profile.getTntTagGame() != null) {
                profile.getTntTagGame().handleLeave(player);
            }

            if (profile.getSumoEvent() != null) {
                profile.getSumoEvent().handleLeave(player);
            }
        }
    }

    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    private void displayStatistics(Player player, Profile profile) {
        player.sendMessage(" ");
        player.sendMessage(CC.translate("&bYour Statistics:"));
        player.sendMessage(CC.translate(" &fKills: &b" + profile.getKills()));
        player.sendMessage(CC.translate(" &fDeaths: &b" + profile.getDeaths()));

        if (profile.getDeaths() == 0) {
            player.sendMessage(CC.translate(" &fKDR: &6Infinity"));
        } else {
            double kdr = (double) profile.getKills() / profile.getDeaths();
            String color = kdr >= 1.0 ? "&a" : "&c";
            player.sendMessage(CC.translate(" &fKDR: " + color + KDR_FORMAT.format(kdr)));
        }

        player.sendMessage(CC.translate(" &fCurrent Killstreak: &b" + profile.getCurrentKillstreak()));
        player.sendMessage(CC.translate(" &fHighest Killstreak: &b" + profile.getHighestKillstreak()));
        player.sendMessage(CC.translate(" &fCredits: &b" + profile.getCredits()));
        player.sendMessage(CC.translate(" &fTier: &7" + profile.getTier().getDisplay() + "✫"));

        if (profile.getBounty() > 0) {
            player.sendMessage(CC.translate(" &fBounty: &b" + profile.getBounty()));
        }

        player.sendMessage(" ");
    }

    private void handlePreviousKit(Player player, Profile profile) {
        String previousKitName = profile.getPreviousKit();

        if (previousKitName == null) {
            player.sendMessage(CC.translate("&cYou don't have a previous kit."));
            return;
        }

        KitsHandler kitsHandler = SoupPvP.getInstance().getKitsHandler();
        Kit currentKit = kitsHandler.getKitByName(profile.getCurrentKit());
        Kit previousKit = kitsHandler.getKitByName(previousKitName);
        Kit defaultKit = kitsHandler.getKitByName("Default");

        if (currentKit == null || defaultKit == null) {
            player.sendMessage(CC.translate("&cError loading kits. Please try again."));
            return;
        }

        if (previousKit == null || !kitsHandler.hasKitUnlocked(profile, previousKit)) {
            profile.setPreviousKit(currentKit.getName());
            profile.setCurrentKit(defaultKit.getName());

            defaultKit.equipKit(player);

            player.sendMessage(CC.translate("&cYour previous kit is no longer available. You were given the &r" + defaultKit.getRarityType().getColor() + defaultKit.getName()));
            return;
        }

        profile.setPreviousKit(currentKit.getName());
        profile.setCurrentKit(previousKit.getName());

        previousKit.equipKit(player);
        player.sendMessage(CC.translate("&aSuccessfully equipped your previous kit: &r" + previousKit.getRarityType().getColor() + previousKit.getName()));
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.SURVIVAL && isInSpawn(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (player.getGameMode() == GameMode.SURVIVAL && isInSpawn(player)) {
            event.setCancelled(true);
        }
    }

    private boolean isInSpawn(Player player) {
        return SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player);
    }
}