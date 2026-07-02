package kami.gg.souppvp.feats.treasurechest.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.treasurechest.TreasureChest;
import kami.gg.souppvp.feats.treasurechest.reward.TreasureChestReward;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreasureChestRewardEditMenu extends Menu {

    private final TreasureChest treasureChest;

    public TreasureChestRewardEditMenu(Player player, TreasureChest treasureChest) {
        super(player, "Edit Rewards - " + treasureChest.getDisplayName(), 36, true);
        this.treasureChest = treasureChest;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 9;
        for (int i = 0; i < treasureChest.getRewards().size(); i++) {
            TreasureChestReward reward = treasureChest.getRewards().get(i);

            buttons.put(slot++, new RewardButton(reward, i));
        }

        buttons.put(4, new BackButton(new TreasureChestMenu(player)));

        // Fill row
        Button filler = getPlaceholderButton();
        for (int i = 0; i < 9; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, filler);
            }
        }

        return buttons;
    }

    @Override
    public void onClickOwn(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        event.setCancelled(true);

        // Add new reward with default values
        TreasureChestReward newReward = new TreasureChestReward(
                clickedItem.clone(),
                10.0,
                "none",
                true,
                false
        );

        treasureChest.getRewards().add(newReward);
        saveData();
        player.sendMessage(CC.t("&aReward added to treasure chest!"));
        update();
    }

    private void saveData() {
        SoupPvP.getInstance().getTreasureChestHandler().saveData();
    }

    private class RewardButton extends Button {
        private final TreasureChestReward reward;
        private final int index;

        public RewardButton(TreasureChestReward reward, int index) {
            this.reward = reward;
            this.index = index;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            lore.add("&b┃ &fChance: &b" + reward.getChance() + "%");
            lore.add("&b┃ &fGrant Item: " + (reward.isGrantItem() ? "&aYes" : "&cNo"));
            lore.add("&b┃ &fBroadcast: " + (reward.isBroadcast() ? "&aYes" : "&cNo"));
            lore.add("&b┃ &fCommand: &f" + (reward.getCommand().equals("none") ? "&cNone" : reward.getCommand()));
            lore.add("");
            lore.add("&eLeft Click &7to Edit Chance");
            lore.add("&eRight Click &7to Toggle Broadcast");
            lore.add("&eShift + Left Click &7to Toggle Grant Item");
            lore.add("&eShift + Right Click &7to Edit Command");
            lore.add("&4Drop (Q) &7to Remove Item");

            String displayName = reward.getItemStack().hasItemMeta() && reward.getItemStack().getItemMeta().hasDisplayName() ? reward.getItemStack().getItemMeta().getDisplayName() : reward.getItemStack().getType().name();

            return new ItemBuilder(reward.getItemStack().clone())
                    .name(displayName)
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (clickType == ClickType.LEFT) {
                // Edit chance
                player.closeInventory();
                playNeutral(player);
                Conversation conversation = new ConversationFactory(SoupPvP.getInstance())
                        .withModality(true)
                        .withPrefix(context -> CC.t("&eEnter new chance (0-100): "))
                        .withFirstPrompt(new NumericPrompt() {
                            @Override
                            protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                                double chance = input.doubleValue();
                                if (chance < 0 || chance > 100) {
                                    playFail(player);
                                    context.getForWhom().sendRawMessage(CC.t("&cChance must be between 0 and 100."));
                                    return this;
                                }
                                reward.setChance(chance);
                                saveData();
                                playSuccess(player);
                                context.getForWhom().sendRawMessage(CC.t("&aChance set to: " + chance + "%"));
                                new TreasureChestRewardEditMenu(player, treasureChest).open();
                                return Prompt.END_OF_CONVERSATION;
                            }

                            @Override
                            protected String getFailedValidationText(ConversationContext context, Number input) {
                                playFail(player);
                                return CC.t("&cInvalid number. Please enter a valid chance (0-100).");
                            }

                            @Override
                            public String getPromptText(ConversationContext context) {
                                return CC.t("&eEnter new chance (0-100) or type 'cancel' to cancel:");
                            }

                            @Override
                            protected boolean isNumberValid(ConversationContext context, Number input) {
                                return true;
                            }
                        })
                        .addConversationAbandonedListener(event -> {
                            if (event.gracefulExit()) return;
                            sendMessage(player, "&cCancelled.");
                            new TreasureChestRewardEditMenu(player, treasureChest).open();
                        })
                        .buildConversation(player);
                player.beginConversation(conversation);

            } else if (clickType == ClickType.SHIFT_LEFT) {
                // Toggle grant item
                reward.setGrantItem(!reward.isGrantItem());
                saveData();
                playNeutral(player);
                sendMessage(player, "&aGrant Item set to: " + (reward.isGrantItem() ? "Yes" : "No"));
                update();

            } else if (clickType == ClickType.RIGHT) {
                // Toggle broadcast
                reward.setBroadcast(!reward.isBroadcast());
                saveData();
                playNeutral(player);
                sendMessage(player, "&aBroadcast set to: " + (reward.isBroadcast() ? "Yes" : "No"));
                update();

            } else if (clickType == ClickType.SHIFT_RIGHT) {
                // Edit command
                player.closeInventory();
                playNeutral(player);
                Conversation conversation = new ConversationFactory(SoupPvP.getInstance())
                        .withModality(true)
                        .withPrefix(context -> CC.t("&eEnter command (without /): "))
                        .withFirstPrompt(new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return CC.t("&eEnter command (without /) or type 'none' to clear, or 'cancel' to cancel:");
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                if (input.equalsIgnoreCase("cancel")) {
                                    context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                                    playFail(player);
                                    new TreasureChestRewardEditMenu(player, treasureChest).open();
                                    return Prompt.END_OF_CONVERSATION;
                                }

                                reward.setCommand(input.equalsIgnoreCase("none") ? "none" : input);
                                saveData();
                                playSuccess(player);
                                context.getForWhom().sendRawMessage(CC.t("&aCommand set to: " + reward.getCommand()));
                                new TreasureChestRewardEditMenu(player, treasureChest).open();
                                return Prompt.END_OF_CONVERSATION;
                            }
                        })
                        .addConversationAbandonedListener(event -> {
                            if (event.gracefulExit()) return;
                            sendMessage(player, "&cCancelled.");
                            new TreasureChestRewardEditMenu(player, treasureChest).open();
                        })
                        .buildConversation(player);
                player.beginConversation(conversation);

            } else if (clickType == ClickType.DROP) {
                // Remove reward
                treasureChest.getRewards().remove(index);
                saveData();
                playFail(player);
                sendMessage(player, "&cReward removed!");
                update();
            }
        }
    }
}
