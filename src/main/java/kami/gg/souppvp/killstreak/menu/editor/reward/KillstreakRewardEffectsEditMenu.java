package kami.gg.souppvp.killstreak.menu.editor.reward;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.KillstreaksHandler;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakEditMenu;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakEffectTypeSelectMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KillstreakRewardEffectsEditMenu extends Menu {

    private final ConfigurableKillstreak killstreak;
    private final int listIndex;

    public KillstreakRewardEffectsEditMenu(ConfigurableKillstreak killstreak, int listIndex, Player player) {
        super(player, "Edit Reward Effects", 36, false);
        this.killstreak = killstreak;
        this.listIndex = listIndex;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        final List<Map<String, Object>> effects;
        if (killstreak.getRewardData().getEffects() == null) {
            effects = new ArrayList<>();
            killstreak.getRewardData().setEffects(effects);
        } else {
            effects = killstreak.getRewardData().getEffects();
        }

        int slot = 9;
        for (int i = 0; i < effects.size(); i++) {
            if (slot >= 35) break;
            
            final int index = i;
            Map<String, Object> effect = effects.get(i);
            
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    String type = (String) effect.get("type");
                    int amplifier = (int) effect.get("amplifier");
                    int duration = (int) effect.get("duration");
                    
                    return new ItemBuilder(Material.POTION)
                            .name("&b" + type)
                            .lore(
                                    "&b┃ &fAmplifier: &f" + (amplifier + 1),
                                    "&b┃ &fDuration: &f" + (duration == Integer.MAX_VALUE ? "Permanent" : duration + " ticks"),
                                    "",
                                    "&cClick to remove"
                            )
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    effects.remove(index);
                    saveKillstreak();
                    playFail(player);
                    player.sendMessage(CC.t("&aRemoved effect."));
                    update();
                }
            });
            slot++;
            if (slot == 17 || slot == 26) slot += 2;
        }

        buttons.put(3, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BREWING_STAND_ITEM)
                        .name("&aAdd Effect")
                        .lore(
                                "&fClick to add a new",
                                "&fpotion effect"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KillstreakEffectTypeSelectMenu(killstreak, listIndex, player).open();
            }
        });

        if (listIndex == -1) {
            buttons.put(4, new Button() {
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
            buttons.put(4, new BackButton(new KillstreakEditMenu(player, killstreak, listIndex)));
        }

        buttons.put(5, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BARRIER)
                        .name("&c&lClear All Effects")
                        .lore(
                                "&cClick to clear all",
                                "&cpotion effects"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                effects.clear();
                saveKillstreak();
                player.sendMessage(CC.t("&aAll effects cleared!"));
                playFail(player);
                update();
            }
        });

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
