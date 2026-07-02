package kami.gg.souppvp.killstreak.menu.editor;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.KillstreaksHandler;
import kami.gg.souppvp.killstreak.menu.editor.reward.KillstreakRewardEffectsEditMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KillstreakEffectTypeSelectMenu extends Menu {

    private final ConfigurableKillstreak killstreak;
    private final int listIndex;

    public KillstreakEffectTypeSelectMenu(ConfigurableKillstreak killstreak, int listIndex, Player player) {
        super(player, "Select Effect Type", 54, false);
        this.killstreak = killstreak;
        this.listIndex = listIndex;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 10;
        for (PotionEffectType type : PotionEffectType.values()) {
            if (type == null) continue;
            if (slot >= 44) break;
            
            final PotionEffectType effectType = type;
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.POTION)
                            .name("&e" + type.getName())
                            .lore("&aClick to start adding this effect!")
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    player.closeInventory();
                    new ConversationFactory(SoupPvP.getInstance())
                            .withModality(true)
                            .withPrefix(context -> CC.t("&eConfiguring " + effectType.getName() + ": "))
                            .withFirstPrompt(new NumericPrompt() {
                                @Override
                                protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                                    context.setSessionData("amplifier", input.intValue());
                                    return new NumericPrompt() {
                                        @Override
                                        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                                            int duration = input.intValue();
                                            int amplifier = (int) context.getSessionData("amplifier");

                                            // Create effect (use Integer.MAX_VALUE for permanent if 0 or negative)
                                            int finalDuration = (duration <= 0) ? Integer.MAX_VALUE : duration * 20; // Convert seconds to ticks

                                            Map<String, Object> effect = new HashMap<>();
                                            effect.put("type", effectType.getName());
                                            effect.put("duration", finalDuration);
                                            effect.put("amplifier", amplifier);

                                            List<Map<String, Object>> effects = killstreak.getRewardData().getEffects();
                                            if (effects == null) {
                                                effects = new ArrayList<>();
                                                killstreak.getRewardData().setEffects(effects);
                                            }
                                            effects.add(effect);
                                            saveKillstreak();

                                            context.getForWhom().sendRawMessage(CC.t("&aEffect added: " + effectType.getName() + " (Amp: " + amplifier + ", Dur: " + (finalDuration == Integer.MAX_VALUE ? "Permanent" : duration + "s") + ")"));

                                            playSuccess((Player) context.getForWhom());
                                            new KillstreakRewardEffectsEditMenu(killstreak, listIndex, (Player) context.getForWhom()).open();
                                            return Prompt.END_OF_CONVERSATION;
                                        }

                                        @Override
                                        protected String getFailedValidationText(ConversationContext context, Number input) {
                                            return CC.t("&cInvalid number. Please enter duration in seconds (0 for permanent).");
                                        }

                                        @Override
                                        public String getPromptText(ConversationContext context) {
                                            return CC.t("&eEnter duration in seconds (0 for permanent, or type 'cancel'):");
                                        }

                                        @Override
                                        protected boolean isNumberValid(ConversationContext context, Number input) {
                                            return input.intValue() >= 0;
                                        }
                                    };
                                }

                                @Override
                                protected String getFailedValidationText(ConversationContext context, Number input) {
                                    return CC.t("&cInvalid amplifier. Please enter a number between 0 and 255.");
                                }

                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return CC.t("&eEnter amplifier (0-255, or type 'cancel'):");
                                }

                                @Override
                                protected boolean isNumberValid(ConversationContext context, Number input) {
                                    int amp = input.intValue();
                                    return amp >= 0 && amp <= 255;
                                }
                            })
                            .addConversationAbandonedListener(event -> {
                                if (event.gracefulExit()) return;
                                playFail(player);
                                player.sendMessage(CC.t("&cCancelled."));
                                new KillstreakRewardEffectsEditMenu(killstreak, listIndex, player).open();
                            })
                            .buildConversation(player)
                            .begin();
                }
            });
            slot++;
            if (slot == 17 || slot == 26 || slot == 35) slot += 2;
        }

        buttons.put(53, new BackButton(new KillstreakRewardEffectsEditMenu(killstreak, listIndex, player)));

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
