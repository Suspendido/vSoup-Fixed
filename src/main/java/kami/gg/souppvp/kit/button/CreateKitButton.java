package kami.gg.souppvp.kit.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.CustomKit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.kit.menu.KitsSelectMenu;
import kami.gg.souppvp.kit.menu.editor.KitEditorMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CreateKitButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.SKULL_ITEM)
                .name("&a&lCreate Kit")
                .data(3)
                .setHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19")
                .lore("&eClick to create a new kit.")
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();
        playNeutral(player);

        new ConversationFactory(SoupPvP.getInstance())
                .withModality(true)
                .withPrefix(context -> CC.t("&eKit Creator &8» "))
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return CC.t("&eEnter the name for the new kit (or type 'cancel' to cancel):");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        if (input.equalsIgnoreCase("cancel")) {
                            context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                            playFail(player);
                            new KitsSelectMenu(player).open();
                            return Prompt.END_OF_CONVERSATION;
                        }

                        if (SoupPvP.getInstance().getKitsHandler().getKitByName(input) != null) {
                            context.getForWhom().sendRawMessage(CC.t("&cA kit with that name already exists!"));
                            new KitsSelectMenu(player).open();
                            playFail(player);
                            return Prompt.END_OF_CONVERSATION;
                        }

                        CustomKit newKit = new CustomKit(
                                input,
                                KitRarity.COMMON,
                                0,
                                new ItemStack(Material.DIAMOND_SWORD),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ItemStack[4],
                                new ArrayList<>(),
                                null,
                                null
                        );

                        SoupPvP.getInstance().getKitsHandler().saveCustomKit(newKit);
                        context.getForWhom().sendRawMessage(CC.t("&aKit &f" + input + " &acreated successfully!"));
                        playSuccess(player);
                        new KitEditorMenu(newKit, player).open();
                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .addConversationAbandonedListener(event -> {
                    if (event.gracefulExit()) return;
                    sendMessage(player, "&cCancelled.");
                    playFail(player);
                    new KitsSelectMenu(player).open();
                })
                .buildConversation(player)
                .begin();
    }
}