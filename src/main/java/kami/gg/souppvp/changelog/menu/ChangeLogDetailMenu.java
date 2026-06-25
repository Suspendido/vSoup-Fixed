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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeLogDetailMenu extends Menu {

    private final ChangeLogHandler changeLogHandler;
    private final ChangeLog changeLog;

    public ChangeLogDetailMenu(ChangeLog changeLog, Player player) {
        super(player, "ChangeLog: " + changeLog.getTitle(), 45, false);
        this.changeLogHandler = SoupPvP.getInstance().getChangeLogHandler();
        this.changeLog = changeLog;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        // Info button
        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("&b┃ &fAuthor: &e" + changeLog.getAuthor());
                lore.add("&b┃ &fCreated: &e" + changeLog.getFormattedDate());
                lore.add("&b┃ &fUpdated: &e" + changeLog.getFormattedUpdateDate());
                lore.add("&b┃ &fLines: &e" + changeLog.getContent().size());

                return new ItemBuilder(Material.SIGN)
                        .name("&b&l" + changeLog.getTitle())
                        .lore(lore)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                // Do nothing
            }
        });

        // Content display
        int slot = 9;
        for (String line : changeLog.getContent()) {
            final String contentLine = line;
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.PAPER)
                            .name("&f" + contentLine)
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    // Do nothing
                }
            });
            slot++;
            if (slot == 36) break;
        }

        // Mark as read button
        buttons.put(40, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.ARROW)
                        .name("&c&lBack")
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                changeLogHandler.markAsRead(player.getUniqueId(), changeLog.getId());
                new ChangeLogViewMenu(player).open();
            }
        });

        return buttons;
    }
}
