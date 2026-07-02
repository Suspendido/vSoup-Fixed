package kami.gg.souppvp.killstreak.menu.editor;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.KillstreaksHandler;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import kami.gg.souppvp.util.menu.menus.ConfirmMenu;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KillstreakLoreEditMenu extends Menu {

    private final ConfigurableKillstreak killstreak;
    private final int listIndex;
    private final KillstreaksHandler handler;

    public KillstreakLoreEditMenu(ConfigurableKillstreak killstreak, int listIndex, Player player) {
        super(player, "Edit Lore", 36, false);
        this.killstreak = killstreak;
        this.listIndex = listIndex;
        this.handler = SoupPvP.getInstance().getKillstreaksHandler();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                int selected = getSelectedLine(player, killstreak);
                List<String> lore = new ArrayList<>();
                lore.add("&7Current lore:");
                lore.add("");

                if (killstreak.getLore().isEmpty()) {
                    lore.add("&8No lines yet.");
                } else {
                    for (int i = 0; i < killstreak.getLore().size(); i++) {
                        if (i == selected) {
                            lore.add("&a▶ &b" + (i + 1) + ". &f" + killstreak.getLore().get(i));
                        } else {
                            lore.add("&7  &7" + (i + 1) + ". &f" + killstreak.getLore().get(i));
                        }
                    }
                }

                lore.add("");
                lore.add("&eLeft-Click &7to cycle selected line.");
                lore.add("&eRight-Click &7to edit selected line.");
                lore.add("&eShift-Left &7to add a new line.");
                lore.add("&eShift-Right &7to delete selected line.");
                lore.add("&4Drop (Q) &7to clear all lines.");

                return new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name("&b&lEdit Lore")
                        .lore(lore)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                int selected = getSelectedLine(player, killstreak);

                if (clickType == ClickType.LEFT) {
                    if (killstreak.getLore().isEmpty()) {
                        sendMessage(player, "&cNo lines to cycle through!");
                        playFail(player);
                        return;
                    }
                    int next = (selected + 1) % killstreak.getLore().size();
                    setSelectedLine(player, next);
                    playNeutral(player);
                    update();

                } else if (clickType == ClickType.RIGHT) {
                    if (killstreak.getLore().isEmpty() || selected == -1) {
                        sendMessage(player, "&cNo line selected!");
                        playFail(player);
                        return;
                    }
                    player.closeInventory();
                    new ConversationFactory(SoupPvP.getInstance())
                            .withModality(true)
                            .withPrefix(context -> CC.t("&eLore &8» "))
                            .withFirstPrompt(new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return CC.t("&eEditing line " + (selected + 1) + ": &f" + killstreak.getLore().get(selected) + "\n&eEnter new text (or 'cancel' to cancel):");
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext context, String input) {
                                    if (input.equalsIgnoreCase("cancel")) {
                                        context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                                        update();
                                        return Prompt.END_OF_CONVERSATION;
                                    }
                                    killstreak.getLore().set(selected, input);
                                    saveKillstreak();
                                    context.getForWhom().sendRawMessage(CC.t("&aLine " + (selected + 1) + " updated: &f" + input));
                                    update();
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            })
                            .buildConversation(player)
                            .begin();

                } else if (clickType == ClickType.SHIFT_LEFT) {
                    player.closeInventory();
                    new ConversationFactory(SoupPvP.getInstance())
                            .withModality(true)
                            .withPrefix(context -> CC.t("&eLore &8» "))
                            .withFirstPrompt(new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return CC.t("&eEnter new line (or 'cancel' to cancel):");
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext context, String input) {
                                    if (input.equalsIgnoreCase("cancel")) {
                                        context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                                        update();
                                        return Prompt.END_OF_CONVERSATION;
                                    }
                                    killstreak.getLore().add(input);
                                    saveKillstreak();
                                    context.getForWhom().sendRawMessage(CC.t("&aLine added: &f" + input));
                                    update();
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            })
                            .buildConversation(player)
                            .begin();

                } else if (clickType == ClickType.SHIFT_RIGHT) {
                    if (killstreak.getLore().isEmpty() || selected == -1) {
                        sendMessage(player, "&cNo line selected!");
                        playFail(player);
                        return;
                    }
                    String removed = killstreak.getLore().remove(selected);
                    setSelectedLine(player, Math.max(0, selected - 1));
                    saveKillstreak();
                    sendMessage(player, "&cRemoved line: &f" + removed);
                    playFail(player);
                    update();

                } else if (clickType == ClickType.DROP) {
                    if (killstreak.getLore().isEmpty()) {
                        sendMessage(player, "&cLore is already empty!");
                        playFail(player);
                        return;
                    }
                    new ConfirmMenu("&4Clear all lore lines?", confirmed -> {
                        if (!confirmed) {
                            update();
                            return;
                        }
                        killstreak.getLore().clear();
                        selectedLine.remove(player.getUniqueId());
                        saveKillstreak();
                        sendMessage(player, "&aLore cleared!");
                        update();
                    }, player).open();
                }
            }
        });

        buttons.put(35, new BackButton(new KillstreakEditMenu(player, killstreak, listIndex)));

        setFillEnabled(true);
        return buttons;
    }

    private void saveKillstreak() {
        handler.getConfig().saveKillstreak(killstreak);
    }

    private static final Map<UUID, Integer> selectedLine = new HashMap<>();

    public static int getSelectedLine(Player player, ConfigurableKillstreak killstreak) {
        if (killstreak.getLore().isEmpty()) return -1;
        int line = selectedLine.getOrDefault(player.getUniqueId(), 0);
        return Math.min(line, killstreak.getLore().size() - 1);
    }

    public static void setSelectedLine(Player player, int line) {
        selectedLine.put(player.getUniqueId(), line);
    }
}
