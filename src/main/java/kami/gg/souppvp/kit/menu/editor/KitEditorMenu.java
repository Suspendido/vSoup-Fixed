package kami.gg.souppvp.kit.menu.editor;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.CustomKit;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitEditorMenu extends Menu {

    private Kit kit;
    private final KitStorage kitStorage;

    public KitEditorMenu(Kit kit, Player player) {
        super(player, "Editing Kit " + kit.getRarityType().getColor() + kit.getName(), 45, false);
        this.kit = kit;
        this.kitStorage = SoupPvP.getInstance().getKitStorage();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();

                lore.add("&b┃ &fRarity: " + kit.getRarityType().getColor() + kit.getRarityType().getName());
                lore.add("&b┃ &fStatus: " + (kit.isEnabled() ? "&aEnabled" : "&cDisabled"));
                lore.add("");
                lore.add("&bKit Properties:");
                lore.add("&b┃ &fPrice: &b" + kit.getPrice());

                if (kit.getPrimaryAbility() != null) {
                    lore.add("&b┃ &fPrimary Ability: " + kit.getPrimaryAbility().getColor() + kit.getPrimaryAbility().getName());
                } else {
                    lore.add("&b┃ &fPrimary Ability: &cNone");
                }

                if (kit.getSecondaryAbility() != null) {
                    lore.add("&b┃ &fSecondary Ability: " + kit.getSecondaryAbility().getColor() + kit.getSecondaryAbility().getName());
                } else {
                    lore.add("&b┃ &fSecondary Ability: &cNone");
                }

                return new ItemBuilder(kit.getIcon())
                        .name(kit.getRarityType().getColor() + kit.getName())
                        .lore(lore)
                        .build();
            }
        });

        buttons.put(19, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(kit.isEnabled() ? Material.REDSTONE_TORCH_ON : Material.LEVER)
                        .name("&b&lToggle Enabled")
                        .lore(
                                "&b┃ &fStatus: " + (kit.isEnabled() ? "&aEnabled" : "&cDisabled"),
                                "",
                                "&aClick to toggle"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                kit.setEnabled(!kit.isEnabled());

                if (kit.isEnabled()) {
                    playSuccess(player);
                } else {
                    playFail(player);
                }

                saveKit();
                sendMessage(player, "&aKit " + kit.getRarityType().getColor() + kit.getName() + "&a is now " + (kit.isEnabled() ? "&lEnabled" : "&c&lDisabled") + "&a.");
                update();
            }
        });

        buttons.put(20, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.PAINTING)
                        .name("&b&lEdit Name")
                        .lore(
                                "&b┃ &fCurrent: " + kit.getRarityType().getColor() + kit.getName(),
                                "",
                                "&aClick to change name"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                player.closeInventory();
                playNeutral(player);
                Conversation conversation = new ConversationFactory(SoupPvP.getInstance())
                        .withModality(true)
                        .withPrefix(context -> CC.t("&eEnter new kit name: "))
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return CC.t("&eEnter new kit name (or type 'cancel' to cancel):");
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                if (input.equalsIgnoreCase("cancel")) {
                                    context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                                    playFail(player);
                                    return Prompt.END_OF_CONVERSATION;
                                }
                                
                                kit.setName(input);
                                saveKit();
                                context.getForWhom().sendRawMessage(CC.t("&aKit name changed to: " + input));
                                playSuccess(player);
                                new KitEditorMenu(kit, player).open();
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
                return new ItemBuilder(Material.ENCHANTED_BOOK)
                        .name("&b&lEdit Abilities")
                        .lore(
                                "&b┃ &fPrimary: " + (kit.getPrimaryAbility() != null ? kit.getPrimaryAbility().getColor() + kit.getPrimaryAbility().getName() : "&cNone"),
                                "&b┃ &fSecondary: " + (kit.getSecondaryAbility() != null ? kit.getSecondaryAbility().getColor() + kit.getSecondaryAbility().getName() : "&cNone"),
                                "",
                                "&aClick to edit abilities"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KitAbilitySlotSelectMenu(kit, player).open();
            }
        });

        buttons.put(22, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.WOOL)
                        .data(getWoolDataForRarity(kit.getRarityType()))
                        .name("&b&lEdit Rarity")
                        .lore(
                                "&b┃ &fCurrent: " + kit.getRarityType().getColor() + kit.getRarityType().getName(),
                                "",
                                "&aClick to change rarities"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KitRaritySelectMenu(kit, player).open();
            }
        });

        buttons.put(23, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.SKULL_ITEM)
                        .data(3)
                        .setHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM2ZTk0ZjZjMzRhMzU0NjVmY2U0YTkwZjJlMjU5NzYzODllYjk3MDlhMTIyNzM1NzRmZjcwZmQ0ZGFhNjg1MiJ9fX0=")
                        .name("&b&lEdit Price")
                        .lore(
                                "&b┃ &fCurrent: &b" + kit.getPrice(),
                                "",
                                "&aClick to change the price"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                player.closeInventory();
                Conversation conversation = new ConversationFactory(SoupPvP.getInstance())
                        .withModality(true)
                        .withPrefix(context -> CC.t("&eEnter new price: "))
                        .withFirstPrompt(new NumericPrompt() {
                            @Override
                            protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                                kit.setPrice(input.intValue());
                                saveKit();
                                playSuccess(player);
                                context.getForWhom().sendRawMessage(CC.t("&aPrice changed to: " + input));
                                new KitEditorMenu(kit, player).open();
                                return Prompt.END_OF_CONVERSATION;
                            }

                            @Override
                            protected String getFailedValidationText(ConversationContext context, Number input) {
                                playFail(player);
                                return CC.t("&cInvalid number. Please enter a valid price.");
                            }

                            @Override
                            public String getPromptText(ConversationContext context) {
                                return CC.t("&eEnter new price (or type 'cancel' to cancel):");
                            }

                            @Override
                            protected boolean isNumberValid(ConversationContext context, Number input) {
                                return input.intValue() >= 0;
                            }
                        })
                        .addConversationAbandonedListener(event -> {
                            if (event.gracefulExit()) return;
                            sendMessage(player, "&cCancelled.");
                            playFail(player);
                            new KitEditorMenu(kit, player).open();
                        })
                        .buildConversation(player);
                
                player.beginConversation(conversation);
            }
        });

        buttons.put(24, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.ITEM_FRAME)
                        .name("&b&lEdit Icon")
                        .lore(
                                "&aClick to change icon",
                                "&aHold item in hand and click"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                ItemStack handItem = player.getItemInHand();
                if (handItem == null || handItem.getType() == Material.AIR) {
                    sendMessage(player, "&cYou must hold an item in your hand!");
                    playFail(player);
                    return;
                }
                
                kit.setIcon(handItem.clone());
                saveKit();
                playSuccess(player);
                sendMessage(player, "&aIcon updated!");
                update();
            }
        });

        buttons.put(25, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                List<String> lore = new ArrayList<>();

                lore.addAll(kit.getDescription());

                lore.add("");
                lore.add("&aClick to edit description");

                return new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name("&b&lEdit Description")
                        .lore(lore)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KitDescriptionEditMenu(kit, player).open();
            }
        });

        buttons.put(31, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.CHEST)
                        .name("&b&lEdit Combat Items")
                        .lore(
                                "&b┃ &fItems: &b" + kit.getCombatEquipments().size(),
                                "",
                                "&7Click to edit items"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KitItemsEditMenu(kit, player).open();
            }
        });

        buttons.put(30, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                        .name("&b&lEdit Armor")
                        .lore(
                                "&b┃ &fArmor pieces: &b4",
                                "",
                                "&aClick to edit armor"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KitArmorEditMenu(kit, player).open();
            }
        });

        buttons.put(32, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.POTION)
                        .name("&b&lEdit Potion Effects")
                        .lore(
                                "&b┃ &fEffects: &b" + kit.getPotionEffects().size(),
                                "",
                                "&aClick to edit effects"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KitEffectsEditMenu(kit, player).open();
            }
        });

        buttons.put(36, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BARRIER)
                        .name("&c&lDelete Kit")
                        .lore(
                                "&7This will permanently delete",
                                "&7the kit &f" + kit.getName() + "&7.",
                                "",
                                "&cThis action cannot be undone!",
                                "",
                                "&eClick to delete."
                        ).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                if (!(kit instanceof CustomKit)) {
                    sendMessage(player, "&cYou can only delete custom kits.");
                    playFail(player);
                    return;
                }

                playNeutral(player);
                new ConfirmMenu("&cDelete " + kit.getName() + "?", confirmed -> {
                    if (!confirmed) {
                        new KitEditorMenu(kit, player).open();
                        return;
                    }

                    boolean deleted = SoupPvP.getInstance().getKitsHandler().deleteCustomKit(kit.getName());

                    if (deleted) {
                        sendMessage(player, "&aKit &f" + kit.getName() + " &adeleted successfully.");
                        playSuccess(player);
                        new KitEditSelectMenu(player).open();
                    } else {
                        sendMessage(player, "&cFailed to delete kit &f" + kit.getName() + "&c.");
                        playFail(player);
                        new KitEditorMenu(kit, player).open();
                    }
                }, player).open();
            }
        });

        buttons.put(44, new BackButton(new KitEditSelectMenu(player)));

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
            this.kit = customKit; // Update reference
        } else {
            kitStorage.saveKit(kit);
        }
    }

    private byte getWoolDataForRarity(KitRarity rarity) {
        return switch (rarity) {
            case COMMON -> 8;
            case UNCOMMON -> 5;
            case RARE -> 11;
            case ULTIMATE -> 9;
            case LEGENDARY -> 1;
            case MYTHICAL -> 10;
        };
    }
}
