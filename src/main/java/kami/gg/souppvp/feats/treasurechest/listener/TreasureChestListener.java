package kami.gg.souppvp.feats.treasurechest.listener;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.treasurechest.TreasureChest;
import kami.gg.souppvp.feats.treasurechest.TreasureChestHandler;
import kami.gg.souppvp.feats.treasurechest.menu.TreasureChestMenu;
import kami.gg.souppvp.feats.treasurechest.reward.TreasureChestReward;
import kami.gg.souppvp.lang.Lang;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * Copyright (c) 2026. @Comunidad, made since 1/7/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter @Setter
public class TreasureChestListener implements Listener {
    private SoupPvP instance;

    public TreasureChestListener(SoupPvP instance) {
        this.instance = instance;
    }

    public TreasureChestHandler getTreasureChestHandler() {
        return this.instance.getTreasureChestHandler();
    }

    private TreasureChest currentlyOpening;
    private UUID currentPlayer = null;
    private int chestsOpened = 0;
    private boolean ending = false;
    private List<TreasureChestReward> rewardsWon = new ArrayList<>();
    private List<Location> openedChests = new ArrayList<>();
    private List<Hologram> rewardHolograms = new ArrayList<>();
    private Hologram centralHologram;

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Block block = event.getClickedBlock();

        if (block == null || !block.getType().equals(Material.CHEST)) return;
        if (currentlyOpening == null || !currentlyOpening.getChests().contains(block.getLocation())) return;

        if (currentPlayer == null || !currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            player.sendMessage(Lang.TREASURE_CHEST_ALREADY_USING);
            return;
        }

        if (openedChests.contains(block.getLocation())) {
            player.sendMessage(Lang.TREASURE_CHEST_ALREADY_OPENED);
            return;
        }

        if (ending) {
            player.sendMessage(Lang.TREASURE_CHEST_ALL_OPENED);
            return;
        }

        event.setCancelled(true);

        final Chest chest = (Chest) block.getState();
        final TreasureChestReward treasureChestReward = this.getTreasureChestHandler().openChest(currentlyOpening, chest, player);
        chest.getWorld().playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 1);

        final String item = treasureChestReward.getItemStack().getItemMeta().hasDisplayName() ? treasureChestReward.getItemStack().getItemMeta().getDisplayName() : treasureChestReward.getItemStack().getAmount() + "x " + ItemUtils.getName(treasureChestReward.getItemStack());

        final Hologram hologram = DHAPI.createHologram(
                "reward_" + System.currentTimeMillis(),
                chest.getBlock().getRelative(BlockFace.UP).getLocation().add(0.5, 1, 0.5),
                false,
                List.of(Lang.TREASURE_CHEST_REWARD_HOLOGRAM, item)
        );
        rewardHolograms.add(hologram);

        openedChests.add(chest.getLocation().clone());
        rewardsWon.add(treasureChestReward);
        chestsOpened++;

        if (chestsOpened >= 4) {
            this.end(player);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (currentPlayer == null || !currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            return;
        }

        this.end(player);
    }

    public void end(Player player) {
        ending = true;
        chestsOpened = 0;
        currentPlayer = null;

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
            SoupPvP.getInstance().getServer().getWorld("world").getEntities().stream().filter(it -> it instanceof Item && it.hasMetadata("TREASURE_REWARD")).forEach(Entity::remove);

            for (Location location : currentlyOpening.getChests()) {
                location.getBlock().setType(Material.AIR);

                location.getWorld().playSound(location, Sound.ZOMBIE_WOODBREAK, 1, 1);
            }

            // Delete reward holograms
            for (Hologram hologram : rewardHolograms) {
                hologram.delete();
            }
            rewardHolograms.clear();

            String color = "&c";
            String link = "store.cavepvp.org/category/omega-chests";

            if (currentlyOpening.getId().equalsIgnoreCase("Illuminated")) {
                color = "&b";
                link = "store.cavepvp.org/category/illuminated-chests";
            }

            if (currentlyOpening.getId().equalsIgnoreCase("Treasure")) {
                color = "&c";
                link = "store.cavepvp.org/category/illuminated-chests";
            }

            for (Player onlinePlayer : SoupPvP.getInstance().getServer().getOnlinePlayers()) {
                onlinePlayer.sendMessage(Lang.TREASURE_CHEST_HEADER);
                onlinePlayer.sendMessage(Lang.format(Lang.TREASURE_CHEST_TITLE, "chest_name", currentlyOpening.getDisplayName()));
                onlinePlayer.sendMessage(Lang.format(Lang.TREASURE_CHEST_OPENED_BY, "player", player.getName()));
                onlinePlayer.sendMessage(Lang.TREASURE_CHEST_FOOTER);
                onlinePlayer.sendMessage(Lang.TREASURE_CHEST_REWARDS_HEADER);

                for (TreasureChestReward treasureChestReward : rewardsWon) {
                    final String item = treasureChestReward.getItemStack().getItemMeta().hasDisplayName() ? (treasureChestReward.getItemStack().getAmount() > 1 ?
                            treasureChestReward.getItemStack().getItemMeta().getDisplayName().replace(ChatColor.stripColor(treasureChestReward.getItemStack().getItemMeta().getDisplayName()), "")
                            + treasureChestReward.getItemStack().getAmount() + "x " : "") + treasureChestReward.getItemStack().getItemMeta().getDisplayName() :
                            treasureChestReward.getItemStack().getAmount() + "x " + ItemUtils.getName(treasureChestReward.getItemStack());

                    onlinePlayer.sendMessage(CC.t("   " + item));
                }

                onlinePlayer.sendMessage(Lang.TREASURE_CHEST_FOOTER);
                onlinePlayer.sendMessage(Lang.format(Lang.TREASURE_CHEST_UNLOCKED_AT, "link", link));
                onlinePlayer.sendMessage(Lang.TREASURE_CHEST_HEADER);
            }

            rewardsWon.clear();

            final Location centralLocation = currentlyOpening.getCentralLocation().clone();

            // Delete existing hologram for this treasure chest before creating new one
            Hologram existingHologram = DHAPI.getHologram("treasure_chest_central_" + currentlyOpening.getId());
            if (existingHologram != null) {
                existingHologram.delete();
            }

            centralHologram = DHAPI.createHologram(
                    "treasure_chest_central_" + currentlyOpening.getId(),
                    centralLocation.getBlock().getLocation().add(0.5, 1.4, 0.5),
                    false,
                    Lang.TREASURE_CHEST_HOLOGRAM
            );

            centralLocation.getBlock().setType(Material.ENDER_CHEST);

            currentlyOpening = null;
        }, 20*5);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPickup(PlayerPickupItemEvent event) {
        if (event.getItem().hasMetadata("TREASURE_REWARD")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteractChest(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getAction() == Action.PHYSICAL) {
            return;
        }

        final Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        if (block.getType() != Material.ENDER_CHEST || block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() != Material.DAYLIGHT_DETECTOR) {
            return;
        }

        if (currentlyOpening != null && currentlyOpening.getChests().contains(block.getLocation())) {
            return;
        }

        if (!event.getAction().name().contains("RIGHT") && !event.getAction().name().contains("LEFT")) {
            return;
        }

        if (currentPlayer != null && !currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            player.sendMessage(Lang.TREASURE_CHEST_IN_USE);
            return;
        }

        new TreasureChestMenu(player).open();

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (currentlyOpening == null || currentlyOpening.getCentralLocation() == null || currentPlayer == null || !currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            return;
        }

        final Location location = currentlyOpening.getCentralLocation().clone();

        if (player.getLocation().distance(location.clone()) <= 3) {
            return;
        }

        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());

        event.setTo(location);
    }
}
