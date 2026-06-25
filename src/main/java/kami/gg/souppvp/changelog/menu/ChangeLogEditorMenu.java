package kami.gg.souppvp.changelog.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.changelog.ChangeLog;
import kami.gg.souppvp.changelog.ChangeLogHandler;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.menus.ConfirmMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeLogEditorMenu extends Menu {

    private final ChangeLogHandler changeLogHandler;

    public ChangeLogEditorMenu(Player player) {
        super(player, "ChangeLog Editor", 45, false);
        this.changeLogHandler = SoupPvP.getInstance().getChangeLogHandler();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        List<ChangeLog> changeLogs = changeLogHandler.getChangeLogs();

        int slot = 9;
        for (ChangeLog changeLog : changeLogs) {
            final ChangeLog cl = changeLog;
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    List<String> lore = new java.util.ArrayList<>();
                    lore.add("&b┃ &fAuthor: &e" + cl.getAuthor());
                    lore.add("&b┃ &fCreated: &e" + cl.getFormattedDate());
                    lore.add("&b┃ &fUpdated: &e" + cl.getFormattedUpdateDate());
                    lore.add("");
                    lore.add("&b┃ &fLines: &e" + cl.getContent().size());
                    lore.add("");
                    lore.add("&eLeft click to edit");
                    lore.add("&cRight click to delete");

                    return new ItemBuilder(Material.BOOK)
                            .name("&b&l" + cl.getTitle())
                            .lore(lore)
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    if (clickType == ClickType.RIGHT) {
                        playNeutral(player);
                        new ConfirmMenu("&cDelete " + cl.getTitle() + "?", confirmed -> {
                            if (!confirmed) {
                                new ChangeLogEditorMenu(player).open();
                                return;
                            }

                            changeLogHandler.deleteChangeLog(cl.getId());
                            sendMessage(player, "&aChangeLog deleted successfully.");
                            playSuccess(player);
                            new ChangeLogEditorMenu(player).open();
                        }, player).open();
                    } else {
                        playNeutral(player);
                        new ChangeLogEditMenu(cl, player).open();
                    }
                }
            });
            slot++;
            if (slot == 36) break;
        }

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name("&a&lCreate New ChangeLog")
                        .lore(
                                "&7Create a new changelog entry",
                                "",
                                "&eClick to create"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new ChangeLogCreateMenu(player).open();
            }
        });

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
