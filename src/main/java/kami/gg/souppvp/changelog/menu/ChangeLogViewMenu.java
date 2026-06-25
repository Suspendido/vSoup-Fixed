package kami.gg.souppvp.changelog.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.changelog.ChangeLog;
import kami.gg.souppvp.changelog.ChangeLogHandler;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeLogViewMenu extends Menu {

    private final ChangeLogHandler changeLogHandler;
    private FilterType filterType;

    public ChangeLogViewMenu(Player player) {
        super(player, "ChangeLogs", 45, false);
        this.changeLogHandler = SoupPvP.getInstance().getChangeLogHandler();
        this.filterType = FilterType.ALL;
    }

    private enum FilterType {
        ALL,
        UNREAD,
        READ
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        List<ChangeLog> allChangeLogs = changeLogHandler.getChangeLogs();
        List<ChangeLog> unreadChangeLogs = changeLogHandler.getUnreadChangeLogs(player.getUniqueId());

        List<ChangeLog> filteredChangeLogs = switch (filterType) {
            case ALL -> allChangeLogs;
            case UNREAD -> unreadChangeLogs;
            case READ -> allChangeLogs.stream()
                    .filter(cl -> !unreadChangeLogs.contains(cl))
                    .collect(java.util.stream.Collectors.toList());
        };

        for (int i = 0; i < 9; i++) {
            buttons.putIfAbsent(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }

        // Filter button
        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                Material mat = switch (filterType) {
                    case ALL -> Material.BOOK;
                    case UNREAD -> Material.ENCHANTED_BOOK;
                    case READ -> Material.BOOK_AND_QUILL;
                };

                String name = switch (filterType) {
                    case ALL -> "&e&lAll ChangeLogs";
                    case UNREAD -> "&a&lUnread Only";
                    case READ -> "&7&lRead Only";
                };

                return new ItemBuilder(mat)
                        .name(name)
                        .lore(
                                "&7Showing: &f" + filteredChangeLogs.size() + " changelog(s)",
                                "&7Unread: &a" + unreadChangeLogs.size(),
                                "",
                                "&eClick to cycle filter."
                        )
                        .setGlow(filterType == FilterType.UNREAD)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                filterType = switch (filterType) {
                    case ALL -> FilterType.UNREAD;
                    case UNREAD -> FilterType.READ;
                    case READ -> FilterType.ALL;
                };
                playNeutral(player);
                update();
            }
        });

        // Changelogs
        int slot = 9;
        for (ChangeLog cl : filteredChangeLogs) {
            if (slot == 36) break;
            boolean unread = unreadChangeLogs.contains(cl);

            buttons.put(slot++, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(unread ? Material.ENCHANTED_BOOK : Material.BOOK)
                            .name((unread ? "&a&l" : "&b&l") + cl.getTitle())
                            .lore(
                                    "&b┃ &fAuthor: &e" + cl.getAuthor(),
                                    "&b┃ &fDate: &e" + cl.getFormattedDate(),
                                    "&b┃ &fLines: &e" + cl.getContent().size(),
                                    "&b┃ &fStatus: " + (unread ? "&a&lNEW!" : "&7Read"),
                                    "",
                                    "&eClick to view."
                            )
                            .setGlow(unread)
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    playNeutral(player);
                    new ChangeLogDetailMenu(cl, player).open();
                }
            });
        }

        if (filteredChangeLogs.isEmpty()) {
            String message = switch (filterType) {
                case ALL -> "No changelogs available.";
                case UNREAD -> "No unread changelogs.";
                case READ -> "No read changelogs yet.";
            };

            buttons.put(9, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.BARRIER)
                            .name("&c&lNo ChangeLogs")
                            .lore("&7" + message)
                            .build();
                }
            });
        }

        buttons.put(40, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BARRIER)
                        .name("&c&lClose")
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.closeInventory();
            }
        });

        return buttons;
    }
}
