package kami.gg.souppvp.changelog.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.changelog.ChangeLog;
import kami.gg.souppvp.changelog.ChangeLogHandler;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.menus.ConfirmMenu;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChangeLogCreateMenu extends Menu {

    private final ChangeLogHandler changeLogHandler;
    private String title;
    private final List<String> content;

    public ChangeLogCreateMenu(Player player) {
        super(player, "Create ChangeLog", 45, false);
        this.changeLogHandler = SoupPvP.getInstance().getChangeLogHandler();
        this.title = "";
        this.content = new ArrayList<>();
    }

    public ChangeLogCreateMenu(Player player, String title, List<String> content) {
        super(player, "Create ChangeLog", 45, false);
        this.changeLogHandler = SoupPvP.getInstance().getChangeLogHandler();
        this.title = title;
        this.content = content;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("&b┃ &fTitle: " + (title.isEmpty() ? "&cNot set" : "&e" + title));
                lore.add("&b┃ &fLines: &e" + content.size());
                lore.add("");
                lore.add("&aClick to set title");

                return new ItemBuilder(Material.SIGN)
                        .name("&b&lChangeLog Title")
                        .lore(lore)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.closeInventory();
                playNeutral(player);
                Conversation conversation = new ConversationFactory(SoupPvP.getInstance())
                        .withModality(true)
                        .withPrefix(context -> CC.t("&eEnter changelog title: "))
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return CC.t("&eEnter changelog title (or type 'cancel' to cancel):");
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                if (input.equalsIgnoreCase("cancel")) {
                                    context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                                    playFail(player);
                                    return Prompt.END_OF_CONVERSATION;
                                }

                                title = input;
                                context.getForWhom().sendRawMessage(CC.t("&aTitle set to: " + input));
                                playSuccess(player);
                                new ChangeLogCreateMenu((Player) context.getForWhom(), title, content).open();
                                return Prompt.END_OF_CONVERSATION;
                            }
                        })
                        .buildConversation(player);

                player.beginConversation(conversation);
            }
        });

        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                int selected = getSelectedLine(player, content);
                List<String> lore = new ArrayList<>();
                lore.add("&7Current content:");
                lore.add("");

                if (content.isEmpty()) {
                    lore.add("&8No lines yet.");
                } else {
                    for (int i = 0; i < content.size(); i++) {
                        if (i == selected) {
                            lore.add("&a▶ &b" + (i + 1) + ". &f" + content.get(i));
                        } else {
                            lore.add("&7  &7" + (i + 1) + ". &f" + content.get(i));
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
                        .name("&b&lEdit Content")
                        .lore(lore)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                int selected = getSelectedLine(player, content);

                if (clickType == ClickType.LEFT) {
                    if (content.isEmpty()) {
                        sendMessage(player, "&cNo lines to cycle through!");
                        playFail(player);
                        return;
                    }
                    int next = (selected + 1) % content.size();
                    setSelectedLine(player, next);
                    playNeutral(player);
                    update();

                } else if (clickType == ClickType.RIGHT) {
                    if (content.isEmpty() || selected == -1) {
                        sendMessage(player, "&cNo line selected!");
                        playFail(player);
                        return;
                    }
                    player.closeInventory();
                    new ConversationFactory(SoupPvP.getInstance())
                            .withModality(true)
                            .withPrefix(context -> CC.t("&eContent &8» "))
                            .withFirstPrompt(new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return CC.t("&eEditing line " + (selected + 1) + ": &f" + content.get(selected) + "\n&eEnter new text (or 'cancel' to cancel):");
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext context, String input) {
                                    if (input.equalsIgnoreCase("cancel")) {
                                        context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                                        new ChangeLogCreateMenu((Player) context.getForWhom(), title, content).open();
                                        return Prompt.END_OF_CONVERSATION;
                                    }
                                    content.set(selected, input);
                                    context.getForWhom().sendRawMessage(CC.t("&aLine " + (selected + 1) + " updated: &f" + input));
                                    new ChangeLogCreateMenu((Player) context.getForWhom(), title, content).open();
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            })
                            .buildConversation(player)
                            .begin();

                } else if (clickType == ClickType.SHIFT_LEFT) {
                    player.closeInventory();
                    new ConversationFactory(SoupPvP.getInstance())
                            .withModality(true)
                            .withPrefix(context -> CC.t("&eContent &8» "))
                            .withFirstPrompt(new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return CC.t("&eEnter new line (or 'cancel' to cancel):");
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext context, String input) {
                                    if (input.equalsIgnoreCase("cancel")) {
                                        context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                                        new ChangeLogCreateMenu((Player) context.getForWhom(), title, content).open();
                                        return Prompt.END_OF_CONVERSATION;
                                    }
                                    content.add(input);
                                    context.getForWhom().sendRawMessage(CC.t("&aLine added: &f" + input));
                                    new ChangeLogCreateMenu((Player) context.getForWhom(), title, content).open();
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            })
                            .buildConversation(player)
                            .begin();

                } else if (clickType == ClickType.SHIFT_RIGHT) {
                    if (content.isEmpty() || selected == -1) {
                        sendMessage(player, "&cNo line selected!");
                        playFail(player);
                        return;
                    }
                    String removed = content.remove(selected);
                    setSelectedLine(player, Math.max(0, selected - 1));
                    sendMessage(player, "&cRemoved line: &f" + removed);
                    playFail(player);
                    update();

                } else if (clickType == ClickType.DROP) {
                    if (content.isEmpty()) {
                        sendMessage(player, "&cContent is already empty!");
                        playFail(player);
                        return;
                    }
                    new ConfirmMenu("&4Clear all content lines?", confirmed -> {
                        if (!confirmed) {
                            update();
                            return;
                        }
                        content.clear();
                        selectedLine.remove(player.getUniqueId());
                        sendMessage(player, "&aContent cleared!");
                        update();
                    }, player).open();
                }
            }
        });

        buttons.put(22, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("&7Preview of changelog content:");
                lore.add("");
                for (int i = 0; i < Math.min(content.size(), 5); i++) {
                    lore.add("&e" + (i + 1) + ". &f" + content.get(i));
                }
                if (content.size() > 5) {
                    lore.add("&7... and " + (content.size() - 5) + " more");
                }
                lore.add("");
                lore.add("&aClick to preview all");

                return new ItemBuilder(Material.BOOK)
                        .name("&b&lPreview Content")
                        .lore(lore)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                if (content.isEmpty()) {
                    sendMessage(player, "&cNo content to preview!");
                    playFail(player);
                    return;
                }

                player.closeInventory();
                player.sendMessage(CC.t("&b&lChangeLog Preview:"));
                player.sendMessage(CC.t("&e" + title));
                player.sendMessage("");
                for (String line : content) {
                    player.sendMessage(CC.t("&f- " + line));
                }
                playNeutral(player);
                new ChangeLogCreateMenu(player, title, content).open();
            }
        });

        buttons.put(24, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                boolean canSave = !title.isEmpty() && !content.isEmpty();
                List<String> lore = new ArrayList<>();
                lore.add("&b┃ &fTitle: " + (title.isEmpty() ? "&cNot set" : "&aSet"));
                lore.add("&b┃ &fContent: " + (content.isEmpty() ? "&cEmpty" : "&a" + content.size() + " lines"));
                lore.add("");
                lore.add(canSave ? "&aClick to save" : "&cSet title and content first");

                return new ItemBuilder(canSave ? Material.EMERALD : Material.BARRIER)
                        .name(canSave ? "&a&lSave ChangeLog" : "&c&lCannot Save")
                        .lore(lore)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                if (title.isEmpty() || content.isEmpty()) {
                    sendMessage(player, "&cYou must set a title and add content first!");
                    playFail(player);
                    return;
                }

                String id = "changelog_" + System.currentTimeMillis();
                ChangeLog changeLog = new ChangeLog(id, title, content, player.getName());
                changeLogHandler.addChangeLog(changeLog);

                sendMessage(player, "&aChangeLog created successfully!");
                playSuccess(player);
                new ChangeLogEditorMenu(player).open();
            }
        });

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
                new ChangeLogEditorMenu(player).open();
            }
        });

        return buttons;
    }

    private static final Map<UUID, Integer> selectedLine = new HashMap<>();

    public static int getSelectedLine(Player player, List<String> content) {
        if (content.isEmpty()) return -1;
        int line = selectedLine.getOrDefault(player.getUniqueId(), 0);
        return Math.min(line, content.size() - 1);
    }

    public static void setSelectedLine(Player player, int line) {
        selectedLine.put(player.getUniqueId(), line);
    }
}
