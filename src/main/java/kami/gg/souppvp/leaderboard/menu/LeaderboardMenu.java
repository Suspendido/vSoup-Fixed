package kami.gg.souppvp.leaderboard.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.leaderboard.LeaderboardManager;
import kami.gg.souppvp.leaderboard.LeaderboardType;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LeaderboardMenu extends Menu {

    private LeaderboardType currentType;

    public LeaderboardMenu(LeaderboardType type) {
        this.currentType = type;
    }

    @Override
    public String getTitle(Player player) {
        return CC.translate("&6&lLeaderboard &7- &e" + currentType.getDisplayName());
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        LeaderboardManager lbManager = SoupPvP.getInstance().getLeaderboardManager();
        List<LeaderboardManager.LeaderboardEntry> top = lbManager.getTop(currentType, 28);

        buttons.put(0, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " "));
        buttons.put(1, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " "));
        buttons.put(7, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " "));
        buttons.put(8, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " "));

        int slot = 2;
        for (LeaderboardType type : LeaderboardType.values()) {
            buttons.put(slot++, new CategoryButton(type));
        }

        for (int row = 1; row < 5; row++) {
            int leftSlot = row * 9;
            int rightSlot = row * 9 + 8;
            buttons.put(leftSlot, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " "));
            buttons.put(rightSlot, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " "));
        }

        int entryIndex = 0;
        for (int row = 1; row < 5; row++) {
            for (int col = 1; col < 8; col++) {
                int entrySlot = row * 9 + col;
                if (entryIndex < top.size()) {
                    LeaderboardManager.LeaderboardEntry entry = top.get(entryIndex);
                    buttons.put(entrySlot, new LeaderboardButton(entry, entryIndex + 1));
                    entryIndex++;
                }
            }
        }

        for (int i = 45; i < 54; i++) {
            if (i == 49) {
                int playerPos = lbManager.getPosition(currentType, player.getUniqueId());
                if (playerPos > 28) {
                    buttons.put(49, new PlayerPositionButton(player, playerPos));
                } else {
                    buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " "));
                }
            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " "));
            }
        }

        return buttons;
    }

    private void updateInventory(Player player) {
        Inventory inv = player.getOpenInventory().getTopInventory();

        // Clear only the leaderboard area (área central 7x4)
        for (int row = 1; row < 5; row++) {
            for (int col = 1; col < 8; col++) {
                inv.setItem(row * 9 + col, null);
            }
        }

        // Clear player position slot
        inv.setItem(49, null);

        // Get fresh data
        Map<Integer, Button> buttons = getButtons(player);

        // Update all items
        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue().getButtonItem(player));
        }

        // Update the buttons map
        this.setButtons(buttons);

        player.updateInventory();
    }

    @AllArgsConstructor
    private class CategoryButton extends Button {
        private final LeaderboardType type;

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!type.equals(currentType)) {
                currentType = type;
                updateInventory(player);
            }
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            Material skull = new ItemBuilder(Material.SKULL_ITEM).durability(1).build().getType();
            Material icon = switch (type) {
                case KILLS -> Material.DIAMOND_SWORD;
                case DEATHS -> skull;
                case KDR -> Material.GOLD_SWORD;
                case KILLSTREAK -> Material.BLAZE_POWDER;
                case CREDITS -> Material.GOLD_NUGGET;
            };

            ItemBuilder builder = new ItemBuilder(icon).name((type.equals(currentType) ? "&a&l" : "&7") + type.getDisplayName());

            if (type.equals(currentType)) {
                builder.lore(
                        "",
                        "&a▶ Currently viewing"
                );
            } else {
                builder.lore(
                        "",
                        "&eClick to view"
                );
            }

            return builder.build();
        }
    }

    @AllArgsConstructor
    private class LeaderboardButton extends Button {
        private final LeaderboardManager.LeaderboardEntry entry;
        private final int position;

        @Override
        public ItemStack getButtonItem(Player player) {
            String color = position == 1 ? "&6&l" : position == 2 ? "&7&l" : position == 3 ? "&c&l" : "&e";
            return new ItemBuilder(Material.SKULL_ITEM)
                    .durability(3)
                    .setSkullOwner(entry.getName())
                    .name(color + "#" + position + " &f" + entry.getName())
                    .lore(
                            "",
                            "&7" + currentType.getDisplayName() + ": " + color + entry.getFormattedValue(currentType),
                            ""
                    )
                    .build();
        }
    }

    @AllArgsConstructor
    private static class PlayerPositionButton extends Button {
        private final Player player;
        private final int position;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SKULL_ITEM)
                    .durability(3)
                    .setSkullOwner(player.getName())
                    .name("&e&lYour Position")
                    .lore(
                            "",
                            "&7Rank: &e#" + position,
                            ""
                    )
                    .build();
        }
    }
}