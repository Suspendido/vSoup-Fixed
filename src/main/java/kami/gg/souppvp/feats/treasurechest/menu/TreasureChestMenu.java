package kami.gg.souppvp.feats.treasurechest.menu;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.treasurechest.TreasureChest;
import kami.gg.souppvp.feats.treasurechest.listener.TreasureChestListener;
import kami.gg.souppvp.lang.Lang;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2026. @Comunidad, made since 1/7/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class TreasureChestMenu extends Menu {

    public TreasureChestMenu(Player player) {
        super(player, "Treasure Chests", 27, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        for (TreasureChest treasureChest : SoupPvP.getInstance().getTreasureChestHandler().getTreasureChests()) {
            int chests = treasureChest.getCache().getOrDefault(player.getUniqueId(), 0);

            buttons.put(treasureChest.getSlot(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    List<String> lore = new ArrayList<>();

                    // Add base lore
                    for (String line : Lang.TREASURE_CHEST_BUTTON_LORE) {
                        lore.add(Lang.format(line, "amount", String.valueOf(chests), "chest_name", treasureChest.getDisplayName()));
                    }

                    // Add admin options if has permission
                    if (player.hasPermission("souppvp.treasurechest")) {
                        lore.add(Lang.TREASURE_CHEST_BUTTON_ADMIN);
                    }

                    // Add purchase or open option based on chest count
                    if (chests == 0) {
                        lore.add(Lang.format(Lang.TREASURE_CHEST_BUTTON_NO_CHESTS, "amount", String.valueOf(chests), "chest_name", treasureChest.getDisplayName()));
                    } else {
                        lore.add(Lang.format(Lang.TREASURE_CHEST_BUTTON_HAS_CHESTS, "amount", String.valueOf(chests), "chest_name", treasureChest.getDisplayName()));
                    }

                    return new ItemBuilder(treasureChest.getMaterial())
                            .name(treasureChest.getDisplayName())
                            .lore(lore)
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                    if (clickType.isShiftClick() && player.hasPermission("souppvp.treasurechest")) {
                        new TreasureChestRewardEditMenu(player, treasureChest).open();
                        return;
                    }

                    if (clickType.isRightClick()) {
                        new TreasureChestLootMenu(player, treasureChest).open();
                        return;
                    }

                    if (!treasureChest.getCache().containsKey(player.getUniqueId()) || treasureChest.getCache().get(player.getUniqueId()) <= 0) {
                        sendMessage(player, Lang.TREASURE_CHEST_NO_CHESTS);
                        return;
                    }

                    if (treasureChest.getRewards().isEmpty()) {
                        sendMessage(player, Lang.TREASURE_CHEST_NO_REWARDS);
                        return;
                    }

                    TreasureChestListener treasureChestListener = SoupPvP.getInstance().getTreasureChestHandler().getTreasureChestListener();

                    if (treasureChestListener.getCurrentPlayer() != null) {
                        player.closeInventory();
                        sendMessage(player, Lang.TREASURE_CHEST_ALREADY_OPENING);
                        return;
                    }

                    Location centralLocation = treasureChest.getCentralLocation();

                    if (centralLocation == null) {
                        return;
                    }

                    // Delete central hologram if exists
                    Hologram centralHologram = DHAPI.getHologram("treasure_chest_central_" + treasureChest.getId());
                    if (centralHologram != null) {
                        centralHologram.delete();
                    }

                    treasureChest.getCache().put(player.getUniqueId(), treasureChest.getCache().get(player.getUniqueId()) - 1);

                    centralLocation.getBlock().setType(Material.AIR);

                    // Clear existing chest blocks before animation
                    for (Location chestLoc : treasureChest.getChests()) {
                        chestLoc.getBlock().setType(Material.AIR);
                    }

                    treasureChestListener.setEnding(false);
                    treasureChestListener.setChestsOpened(0);
                    treasureChestListener.getRewardsWon().clear();
                    treasureChestListener.getOpenedChests().clear();
                    treasureChestListener.setCurrentlyOpening(treasureChest);
                    treasureChestListener.setCurrentPlayer(player.getUniqueId());

                    String color = "&c";

                    if (treasureChest.getId().equalsIgnoreCase("Illuminated")) {
                        color = "&b";
                    }

                    for (Player p : SoupPvP.getInstance().getServer().getOnlinePlayers()) {
                        for (String line : Lang.TREASURE_CHEST_BROADCAST) {
                            String formattedLine = Lang.format(line, "chest_name", treasureChest.getDisplayName(), "player", player.getName(), "color", color);
                            sendMessage(p, formattedLine);
                        }
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            TreasureChestListener listener = SoupPvP.getInstance().getTreasureChestHandler().getTreasureChestListener();

                            if (listener.getCurrentPlayer() == null || listener.isEnding()) {
                                this.cancel();
                                return;
                            }

                            for (Player onlinePlayer : SoupPvP.getInstance().getServer().getOnlinePlayers()) {
                                if (onlinePlayer.getUniqueId().toString().equalsIgnoreCase(listener.getCurrentPlayer().toString())) {
                                    continue;
                                }

                                if (!onlinePlayer.getWorld().getName().equalsIgnoreCase("world")) {
                                    continue;
                                }

                                if (onlinePlayer.getLocation().distance(centralLocation.clone()) <= 4) {
                                    onlinePlayer.setVelocity(onlinePlayer.getLocation().getDirection().multiply(-1.75));
                                }
                            }
                        }
                    }.runTaskTimer(SoupPvP.getInstance(), 20, 20);

                    player.teleport(centralLocation.clone());

                    // Spawn chests with animation immediately
                    SoupPvP.getInstance().getTreasureChestHandler().spawnChestsWithAnimation(treasureChest, () -> {
                        // Animation complete
                        for (Location chest : treasureChest.getChests()) {
                            chest.getWorld().playEffect(chest, Effect.EXPLOSION_HUGE, 1, 1);
                            chest.getWorld().playSound(chest, Sound.ANVIL_LAND, 1, 1);
                        }

                        sendMessage(player, Lang.TREASURE_CHEST_CHESTS_APPEARED);
                    });
                }
            });
        }

        setFillEnabled(true);
        return buttons;
    }
}
