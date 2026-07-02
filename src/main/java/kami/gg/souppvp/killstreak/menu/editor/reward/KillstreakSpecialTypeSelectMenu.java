package kami.gg.souppvp.killstreak.menu.editor.reward;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.KillstreaksHandler;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakEditMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KillstreakSpecialTypeSelectMenu extends Menu {

    private final ConfigurableKillstreak killstreak;
    private final int listIndex;

    private static final Map<String, String> SPECIAL_TYPES = Map.of(
        "REPAIR", "Full Repair",
        "NUKE", "Tactical Nuke",
        "GRANDMA_STEW", "Grandma's Stew",
        "ATTACK_DOGS", "Attack Dogs",
        "SECURITY_GUARD", "Security Guard",
        "CARE_PACKAGE", "Care Package"
    );

    public KillstreakSpecialTypeSelectMenu(ConfigurableKillstreak killstreak, int listIndex, Player player) {
        super(player, "Select Special Type", 36, false);
        this.killstreak = killstreak;
        this.listIndex = listIndex;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 9;
        for (Map.Entry<String, String> entry : SPECIAL_TYPES.entrySet()) {
            final String typeKey = entry.getKey();
            final String displayName = entry.getValue();
            
            Material icon = switch (typeKey) {
                case "REPAIR" -> Material.ANVIL;
                case "NUKE" -> Material.TNT;
                case "GRANDMA_STEW" -> Material.MUSHROOM_SOUP;
                case "ATTACK_DOGS" -> Material.BONE;
                case "SECURITY_GUARD" -> Material.IRON_CHESTPLATE;
                case "CARE_PACKAGE" -> Material.CHEST;
                default -> Material.PAPER;
            };

            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    boolean isSelected = typeKey.equals(killstreak.getRewardData().getSpecialType());
                    
                    return new ItemBuilder(icon)
                            .name((isSelected ? "&a&l" : "&e") + displayName)
                            .lore(
                                    "&b┃ &fType: &e" + typeKey,
                                    "",
                                    isSelected ? "&aCurrently selected" : "&aClick to select"
                            )
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    killstreak.getRewardData().setSpecialType(typeKey);
                    saveKillstreak();
                    playSuccess(player);
                    sendMessage(player, "&aSelected: " + displayName);
                    new KillstreakEditMenu(player, killstreak, listIndex).open();
                }
            });
            slot++;
            if (slot == 17 || slot == 26) slot += 2;
        }

        buttons.put(3, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BARRIER)
                        .name("&c&lClear Special Type")
                        .lore(
                                "&cClick to remove",
                                "&cthe special type"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                killstreak.getRewardData().setSpecialType(null);
                saveKillstreak();
                playFail(player);
                sendMessage(player, "&aSpecial type cleared.");
                new KillstreakEditMenu(player, killstreak, listIndex).open();
            }
        });

        if (listIndex == -1) {
            buttons.put(5, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.ARROW)
                            .name("&cBack")
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    KillstreakCreateMenu createMenu = new KillstreakCreateMenu(player);
                    createMenu.setRewardData(killstreak.getRewardData());
                    createMenu.open();
                }
            });
        } else {
            buttons.put(5, new BackButton(new KillstreakEditMenu(player, killstreak, listIndex)));
        }

        // Fill row
        Button filler = getPlaceholderButton();
        for (int i = 0; i < 9; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, filler);
            }
        }

        return buttons;
    }

    private void saveKillstreak() {
        if (listIndex == -1) {
            // Temporary killstreak, don't save to handler
            return;
        }
        KillstreaksHandler handler = SoupPvP.getInstance().getKillstreaksHandler();
        handler.updateKillstreak(killstreak);
    }
}
