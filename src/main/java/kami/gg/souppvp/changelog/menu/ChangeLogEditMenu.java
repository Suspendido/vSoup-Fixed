package kami.gg.souppvp.changelog.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.changelog.ChangeLog;
import kami.gg.souppvp.changelog.ChangeLogHandler;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeLogEditMenu extends Menu {

    private final ChangeLogHandler changeLogHandler;
    private final ChangeLog changeLog;
    private List<String> content;

    public ChangeLogEditMenu(ChangeLog changeLog, Player player) {
        super(player, "Edit ChangeLog", 45, false);
        this.changeLogHandler = SoupPvP.getInstance().getChangeLogHandler();
        this.changeLog = changeLog;
        this.content = new ArrayList<>(changeLog.getContent());
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("&b┃ &fTitle: &e" + changeLog.getTitle());
                lore.add("&b┃ &fAuthor: &e" + changeLog.getAuthor());
                lore.add("&b┃ &fCreated: &e" + changeLog.getFormattedDate());
                lore.add("&b┃ &fLines: &e" + content.size());
                lore.add("");
                lore.add("&aClick to change title");

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
                        .withPrefix(context -> CC.t("&eEnter new title: "))
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return CC.t("&eEnter new title (or type 'cancel' to cancel):");
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                if (input.equalsIgnoreCase("cancel")) {
                                    context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                                    playFail(player);
                                    return Prompt.END_OF_CONVERSATION;
                                }

                                changeLog.setTitle(input);
                                changeLog.setUpdatedAt(java.time.LocalDateTime.now());
                                changeLogHandler.updateChangeLog(changeLog);
                                context.getForWhom().sendRawMessage(CC.t("&aTitle updated to: " + input));
                                playSuccess(player);
                                new ChangeLogEditMenu(changeLog, (Player) context.getForWhom()).open();
                                return Prompt.END_OF_CONVERSATION;
                            }
                        })
                        .buildConversation(player);

                player.beginConversation(conversation);
            }
        });

        buttons.put(19, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("&b┃ &fCurrent lines: &e" + content.size());
                lore.add("");
                lore.add("&aClick to add a line");

                return new ItemBuilder(Material.PAPER)
                        .name("&b&lAdd Line")
                        .lore(lore)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.closeInventory();
                playNeutral(player);
                Conversation conversation = new ConversationFactory(SoupPvP.getInstance())
                        .withModality(true)
                        .withPrefix(context -> CC.t("&eEnter line content: "))
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return CC.t("&eEnter line content (or type 'cancel' to cancel):");
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                if (input.equalsIgnoreCase("cancel")) {
                                    context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                                    playFail(player);
                                    return Prompt.END_OF_CONVERSATION;
                                }

                                content.add(input);
                                changeLog.setContent(content);
                                changeLog.setUpdatedAt(java.time.LocalDateTime.now());
                                changeLogHandler.updateChangeLog(changeLog);
                                context.getForWhom().sendRawMessage(CC.t("&aLine added!"));
                                playSuccess(player);
                                new ChangeLogEditMenu(changeLog, (Player) context.getForWhom()).open();
                                return Prompt.END_OF_CONVERSATION;
                            }
                        })
                        .buildConversation(player);

                player.beginConversation(conversation);
            }
        });

        buttons.put(21, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add("&b┃ &fCurrent lines: &e" + content.size());
                lore.add("");
                lore.add("&cClick to remove last line");

                return new ItemBuilder(Material.REDSTONE)
                        .name("&c&lRemove Last Line")
                        .lore(lore)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                if (content.isEmpty()) {
                    sendMessage(player, "&cNo lines to remove!");
                    playFail(player);
                    return;
                }

                content.remove(content.size() - 1);
                changeLog.setContent(content);
                changeLog.setUpdatedAt(java.time.LocalDateTime.now());
                changeLogHandler.updateChangeLog(changeLog);
                sendMessage(player, "&aLast line removed.");
                playSuccess(player);
                update();
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
                player.sendMessage(CC.t("&e" + changeLog.getTitle()));
                player.sendMessage("");
                for (String line : content) {
                    player.sendMessage(CC.t("&f- " + line));
                }
                playNeutral(player);
                new ChangeLogEditMenu(changeLog, player).open();
            }
        });

        buttons.put(24, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.EMERALD)
                        .name("&a&lSave Changes")
                        .lore(
                                "&b┃ &fTitle: &e" + changeLog.getTitle(),
                                "&b┃ &fLines: &e" + content.size(),
                                "",
                                "&aClick to save"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                changeLog.setContent(content);
                changeLog.setUpdatedAt(java.time.LocalDateTime.now());
                changeLogHandler.updateChangeLog(changeLog);

                sendMessage(player, "&aChangeLog updated successfully!");
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
}
