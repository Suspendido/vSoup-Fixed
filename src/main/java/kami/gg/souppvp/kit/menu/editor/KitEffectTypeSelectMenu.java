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
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class KitEffectTypeSelectMenu extends Menu {

    private final Kit kit;
    private final KitStorage kitStorage;

    public KitEffectTypeSelectMenu(Kit kit, Player player) {
        super(player, "Select Effect Type", 54, false);
        this.kit = kit;
        this.kitStorage = SoupPvP.getInstance().getKitStorage();
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

                                            PotionEffect effect = new PotionEffect(effectType, finalDuration, amplifier);
                                            kit.getPotionEffects().add(effect);
                                            saveKit();

                                            context.getForWhom().sendRawMessage(CC.t("&aEffect added: " + effectType.getName() + " (Amp: " + amplifier + ", Dur: " + (finalDuration == Integer.MAX_VALUE ? "Permanent" : duration + "s") + ")"));

                                            playSuccess((Player) context.getForWhom());
                                            new KitEffectsEditMenu(kit, (Player) context.getForWhom()).open();
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
                                new KitEffectsEditMenu(kit, player).open();
                            })
                            .buildConversation(player)
                            .begin();
                }
            });
            slot++;
            if (slot == 17 || slot == 26 || slot == 35) slot += 2;
        }

        buttons.put(53, new BackButton(new KitEffectsEditMenu(kit, player)));

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
}
