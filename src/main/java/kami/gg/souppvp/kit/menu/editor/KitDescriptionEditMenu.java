package kami.gg.souppvp.kit.menu.editor;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.CustomKit;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.storage.KitStorage;
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

public class KitDescriptionEditMenu extends Menu {

    private final Kit kit;
    private final KitStorage kitStorage;

    public KitDescriptionEditMenu(Kit kit, Player player) {
        super(player, "Edit Description", 36, false);
        this.kit = kit;
        this.kitStorage = SoupPvP.getInstance().getKitStorage();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                int selected = getSelectedLine(player, kit);
                List<String> lore = new ArrayList<>();
                lore.add("&7Current description:");
                lore.add("");

                if (kit.getDescription().isEmpty()) {
                    lore.add("&8No lines yet.");
                } else {
                    for (int i = 0; i < kit.getDescription().size(); i++) {
                        if (i == selected) {
                            lore.add("&a▶ &b" + (i + 1) + ". &f" + kit.getDescription().get(i));
                        } else {
                            lore.add("&7  &7" + (i + 1) + ". &f" + kit.getDescription().get(i));
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
                        .name("&b&lEdit Description")
                        .lore(lore)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                int selected = getSelectedLine(player, kit);

                if (clickType == ClickType.LEFT) {
                    if (kit.getDescription().isEmpty()) {
                        sendMessage(player, "&cNo lines to cycle through!");
                        playFail(player);
                        return;
                    }
                    int next = (selected + 1) % kit.getDescription().size();
                    setSelectedLine(player, next);
                    playNeutral(player);
                    update();

                } else if (clickType == ClickType.RIGHT) {
                    if (kit.getDescription().isEmpty() || selected == -1) {
                        sendMessage(player, "&cNo line selected!");
                        playFail(player);
                        return;
                    }
                    player.closeInventory();
                    new ConversationFactory(SoupPvP.getInstance())
                            .withModality(true)
                            .withPrefix(context -> CC.t("&eDescription &8» "))
                            .withFirstPrompt(new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return CC.t("&eEditing line " + (selected + 1) + ": &f" + kit.getDescription().get(selected) + "\n&eEnter new text (or 'cancel' to cancel):");
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext context, String input) {
                                    if (input.equalsIgnoreCase("cancel")) {
                                        context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                                        update();
                                        return Prompt.END_OF_CONVERSATION;
                                    }
                                    kit.getDescription().set(selected, input);
                                    saveKit();
                                    context.getForWhom().sendRawMessage(CC.t("&aLine " + (selected + 1) + " updated: &f" + input));
                                    update();
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            })
                            .buildConversation(player)
                            .begin();

                } else if (clickType == ClickType.SHIFT_LEFT) {
                    // añadir nueva linea
                    player.closeInventory();
                    new ConversationFactory(SoupPvP.getInstance())
                            .withModality(true)
                            .withPrefix(context -> CC.t("&eDescription &8» "))
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
                                    kit.getDescription().add(input);
                                    saveKit();
                                    context.getForWhom().sendRawMessage(CC.t("&aLine added: &f" + input));
                                    update();
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            })
                            .buildConversation(player)
                            .begin();

                } else if (clickType == ClickType.SHIFT_RIGHT) {
                    if (kit.getDescription().isEmpty() || selected == -1) {
                        sendMessage(player, "&cNo line selected!");
                        playFail(player);
                        return;
                    }
                    String removed = kit.getDescription().remove(selected);
                    setSelectedLine(player, Math.max(0, selected - 1));
                    saveKit();
                    sendMessage(player, "&cRemoved line: &f" + removed);
                    playFail(player);
                    update();

                } else if (clickType == ClickType.DROP) {
                    if (kit.getDescription().isEmpty()) {
                        sendMessage(player, "&cDescription is already empty!");
                        playFail(player);
                        return;
                    }
                    new ConfirmMenu("&4Clear all description lines?", confirmed -> {
                        if (!confirmed) {
                           update();
                            return;
                        }
                        kit.getDescription().clear();
                        selectedLine.remove(player.getUniqueId());
                        saveKit();
                        sendMessage(player, "&aDescription cleared!");
                        update();
                    }, player).open();
                }
            }
        });

        buttons.put(35, new BackButton(new KitEditorMenu(kit, player)));

        setFillEnabled(true);
        return buttons;
    }

    private void saveKit() {
        // Convert to CustomKit if not already
        if (!(kit instanceof CustomKit)) {
            CustomKit customKit = new CustomKit(
                    kit.getName(),
                    kit.getRarityType(),
                    kit.getPrice(),
                    kit.getIcon(),
                    kit.getDescription(),
                    kit.getCombatEquipments(),
                    kit.getArmor(),
                    kit.getPotionEffects(),
                    kit.getPrimaryAbility(),
                    kit.getSecondaryAbility()
            );
            customKit.setEnabled(kit.isEnabled());
            
            // Replace in handler
            SoupPvP.getInstance().getKitsHandler().getKits().remove(kit);
            SoupPvP.getInstance().getKitsHandler().getKits().add(customKit);
            
            kitStorage.saveKit(customKit);
        } else {
            kitStorage.saveKit(kit);
        }
    }

    private static final Map<UUID, Integer> selectedLine = new HashMap<>();

    public static int getSelectedLine(Player player, Kit kit) {
        if (kit.getDescription().isEmpty()) return -1;
        int line = selectedLine.getOrDefault(player.getUniqueId(), 0);
        return Math.min(line, kit.getDescription().size() - 1); // evita index out of bounds
    }

    public static void setSelectedLine(Player player, int line) {
        selectedLine.put(player.getUniqueId(), line);
    }
}
