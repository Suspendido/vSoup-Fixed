package kami.gg.souppvp.listener.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.menu.HostEventsMenu;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitsHandler;
import kami.gg.souppvp.kit.menu.KitsSelectMenu;
import kami.gg.souppvp.options.OptionsMenu;
import kami.gg.souppvp.perk.menu.AllPerksMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.shop.ShopMenu;
import kami.gg.souppvp.tier.TierCategory;
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
            new KitsSelectMenu(player).open();
            return;
        }

        if (item.isSimilar(SpawnItems.HOST_EVENTS)) {
            new HostEventsMenu(player).open();
            return;
        }

        if (item.isSimilar(SpawnItems.SHOP)) {
            new ShopMenu(player).open();
            return;
        }

        if (item.isSimilar(SpawnItems.YOUR_STATISTICS)) {
            displayStatistics(player, profile);
            return;
        }

        if (item.isSimilar(SpawnItems.PERK_SELECTOR)) {
            new AllPerksMenu(player).open();
            return;
        }

        if (item.isSimilar(SpawnItems.PREVIOUS_KIT)) {
            handlePreviousKit(player, profile);
            return;
        }

        if (item.isSimilar(SpawnItems.YOUR_OPTIONS)) {
            new OptionsMenu(player).open();
            return;
        }

        if (item.isSimilar(EventItems.LEAVE_EVENT)) {
            Event activeEvent = profile.getActiveEvent();
            if (activeEvent != null) {
                activeEvent.handleLeave(player);
            }
        }
    }

    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    private void displayStatistics(Player player, Profile profile) {
        player.sendMessage(" ");
        player.sendMessage(CC.t("&bYour Statistics:"));
        player.sendMessage(CC.t(" &fKills: &b" + profile.getKills()));
        player.sendMessage(CC.t(" &fDeaths: &b" + profile.getDeaths()));

        if (profile.getDeaths() == 0) {
            player.sendMessage(CC.t(" &fKDR: &6Infinity"));
        } else {
            double kdr = (double) profile.getKills() / profile.getDeaths();
            String color = kdr >= 1.0 ? "&a" : "&c";
            player.sendMessage(CC.t(" &fKDR: " + color + KDR_FORMAT.format(kdr)));
        }

        player.sendMessage(CC.t(" &fCurrent Killstreak: &b" + profile.getCurrentKillstreak()));
        player.sendMessage(CC.t(" &fHighest Killstreak: &b" + profile.getHighestKillstreak()));
        player.sendMessage(CC.t(" &fCredits: &b" + profile.getCredits()));
        TierCategory category = TierCategory.getCategoryByName(profile.getSelectedTierIcon());
        player.sendMessage(CC.t(" &fTier: &7" + profile.getTier() + category.getFormattedIcon()));

        if (profile.getBounty() > 0) {
            player.sendMessage(CC.t(" &fBounty: &b" + profile.getBounty()));
        }

        player.sendMessage(" ");
    }

    private void handlePreviousKit(Player player, Profile profile) {
        String previousKitName = profile.getPreviousKit();

        if (previousKitName == null) {
            player.sendMessage(CC.t("&cYou don't have a previous kit."));
            return;
        }

        KitsHandler kitsHandler = SoupPvP.getInstance().getKitsHandler();
        Kit currentKit = kitsHandler.getKitByName(profile.getCurrentKit());
        Kit previousKit = kitsHandler.getKitByName(previousKitName);
        Kit defaultKit = kitsHandler.getKitByName("Default");

        if (currentKit == null || defaultKit == null) {
            player.sendMessage(CC.t("&cError loading kits. Please try again."));
            return;
        }

        if (previousKit == null || !kitsHandler.hasKitUnlocked(profile, previousKit)) {
            profile.setPreviousKit(currentKit.getName());
            profile.setCurrentKit(defaultKit.getName());

            defaultKit.equipKit(player);

            player.sendMessage(CC.t("&cYour previous kit is no longer available. You were given the &r" + defaultKit.getRarityType().getColor() + defaultKit.getName()));
            return;
        }

        profile.setPreviousKit(currentKit.getName());
        profile.setCurrentKit(previousKit.getName());

        previousKit.equipKit(player);
        player.sendMessage(CC.t("&aSuccessfully equipped your previous kit: &r" + previousKit.getRarityType().getColor() + previousKit.getName()));
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