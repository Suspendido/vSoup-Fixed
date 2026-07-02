package kami.gg.souppvp.killstreak.menu.editor.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakCreateMenu;
import kami.gg.souppvp.killstreak.menu.editor.KillstreakEditMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class EditNameButton extends Button {

    private final KillstreakCreateMenu createMenu;
    private final KillstreakEditMenu editMenu;
    private final ConfigurableKillstreak killstreak;
    private final boolean isCreateMode;

    public EditNameButton(KillstreakCreateMenu createMenu) {
        this.createMenu = createMenu;
        this.editMenu = null;
        this.killstreak = null;
        this.isCreateMode = true;
    }

    public EditNameButton(KillstreakEditMenu editMenu, ConfigurableKillstreak killstreak) {
        this.createMenu = null;
        this.editMenu = editMenu;
        this.killstreak = killstreak;
        this.isCreateMode = false;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        String currentName = isCreateMode ? createMenu.getName() : killstreak.getName();
        return new ItemBuilder(Material.PAINTING)
                .name("&b&lEdit Name")
                .lore(
                        "&b┃ &fCurrent: " + (currentName.isEmpty() ? "&cNot set" : "&a" + currentName),
                        "",
                        "&aClick to change name"
                )
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        playNeutral(player);
        player.closeInventory();
        Conversation conversation = new ConversationFactory(SoupPvP.getInstance())
                .withModality(true)
                .withPrefix(context -> CC.t("&eEnter new name: "))
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return CC.t("&eEnter new name (or type 'cancel' to cancel):");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        if (input.equalsIgnoreCase("cancel")) {
                            context.getForWhom().sendRawMessage(CC.t("&cCancelled."));
                            playFail(player);
                            return Prompt.END_OF_CONVERSATION;
                        }

                        if (isCreateMode) {
                            createMenu.setName(input);
                        } else {
                            killstreak.setName(input);
                        }

                        context.getForWhom().sendRawMessage(CC.t("&aName changed to: " + input));
                        playSuccess(player);
                        reopenMenu();
                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .addConversationAbandonedListener(event -> {
                    if (event.gracefulExit()) return;
                    sendMessage(player, "&cCancelled.");
                    playFail(player);
                    reopenMenu();
                })
                .buildConversation(player);

        player.beginConversation(conversation);
    }

    private void reopenMenu() {
        if (isCreateMode) {
            createMenu.open();
        } else {
            editMenu.open();
        }
    }
}
