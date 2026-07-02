package kami.gg.souppvp.killstreak.menu.editor;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.KillstreaksHandler;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.menus.ConfirmMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KillstreakEditorMenu extends Menu {

    public KillstreakEditorMenu(Player player) {
        super(player, "Killstreak Editor", 36, false);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();
        KillstreaksHandler handler = SoupPvP.getInstance().getKillstreaksHandler();
        List<ConfigurableKillstreak> killstreaks = handler.getKillstreaks();

        buttons.put(5, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.SKULL_ITEM)
                        .data(3)
                        .setHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19")
                        .name("&a&lCreate New Killstreak")
                        .lore("&eClick to create a new killstreak")
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KillstreakCreateMenu(player).open();
            }
        });

        buttons.put(3, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.SKULL_ITEM)
                        .data(3)
                        .setHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc5ZThjZjIxYjgzOWIyNTVhMjgzNmUyNTE5NDFjNWZkYzk5YWYwMTU1OWUzNzMzZDUzMjVjY2ZhM2Q5MjJhYSJ9fX0=")
                        .name("&c&lReload Killstreaks")
                        .lore(
                                "&b┃ &fReload from config file",
                                "",
                                "&aClick to reload"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                handler.reload();
                sendMessage(player, "&aKillstreaks reloaded successfully.");
                playSuccess(player);
                update();
            }
        });

        // Add killstreak items
        int slot = 9;
        int listIndex = 0;
        for (ConfigurableKillstreak ks : killstreaks) {
            if (slot >= 45) break; // Leave space for bottom row

            final int index = listIndex;
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&b┃ &fRequired Kills: &d" + ks.getRequiredKills());
                    lore.add("&b┃ &fReward Type: &e" + ks.getRewardType().name());
                    lore.add("");
                    lore.add("&aLeft Click: &eEdit");
                    lore.add("&cRight Click: &eDelete");

                    return new ItemBuilder(ks.getIcon())
                            .name("&a" + ks.getName())
                            .lore(lore)
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    if (clickType == ClickType.LEFT) {
                        playNeutral(player);
                        new KillstreakEditMenu(player, ks, index).open();
                    } else if (clickType == ClickType.RIGHT) {
                        playNeutral(player);
                        new ConfirmMenu("&cDelete " + ks.getName() + "?", confirmed -> {
                            if (!confirmed) {
                                new KillstreakEditorMenu(player).open();
                                return;
                            }

                            handler.removeKillstreak(ks.getId());
                            sendMessage(player, "&aKillstreak deleted successfully.");
                            playSuccess(player);
                            new KillstreakEditorMenu(player).open();
                        }, player).open();
                    }
                }
            });
            slot++;
            listIndex++;
        }

        Button filler = getPlaceholderButton();
        for (int i = 0; i < 9; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, filler);
            }
        }

        return buttons;
    }
}
