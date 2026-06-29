package kami.gg.souppvp.kit.menu.editor;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.kit.ability.KitAbilityRegistry;
import kami.gg.souppvp.kit.storage.KitStorage;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitAbilitySelectMenu extends Menu {

    private final Kit kit;
    private final KitAbilityRegistry abilityRegistry;
    private final KitStorage kitStorage;
    private final boolean isPrimary;

    public KitAbilitySelectMenu(Kit kit, boolean isPrimary, Player player) {
        super(player, isPrimary ? "Select Primary Ability" : "Select Secondary Ability", 45, false);
        this.kit = kit;
        this.isPrimary = isPrimary;
        this.abilityRegistry = SoupPvP.getInstance().getKitAbilityRegistry();
        this.kitStorage = SoupPvP.getInstance().getKitStorage();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        // Add "None" option to remove ability
        KitAbility currentAbility = isPrimary ? kit.getPrimaryAbility() : kit.getSecondaryAbility();
        buttons.put(9, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                boolean selected = currentAbility == null;
                return new ItemBuilder(Material.BARRIER)
                        .name("&c&lNone")
                        .lore(
                                "&7Remove ability from this slot",
                                "",
                                selected ? "&aCurrently selected" : "&eClick to remove ability"
                        )
                        .setGlow(selected)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                if (isPrimary) {
                    kit.setPrimaryAbility(null);
                } else {
                    kit.setSecondaryAbility(null);
                }
                saveKit();
                sendMessage(player, "&a" + (isPrimary ? "Primary" : "Secondary") + " ability removed.");
                playSuccess(player);
                new KitAbilitySlotSelectMenu(kit, player).open();
            }
        });

        int slot = 10;
        for (KitAbility ability : abilityRegistry.getAbilities().values()) {
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    KitAbility currentAbility = isPrimary ? kit.getPrimaryAbility() : kit.getSecondaryAbility();
                    boolean selected = currentAbility != null && currentAbility.getName().equals(ability.getName());
                    
                    // Check if this ability is already in the other slot
                    KitAbility otherSlotAbility = isPrimary ? kit.getSecondaryAbility() : kit.getPrimaryAbility();
                    boolean inOtherSlot = otherSlotAbility != null && otherSlotAbility.getName().equals(ability.getName());
                    
                    return new ItemBuilder(ability.getItem())
                            .name(ability.getColor() + ability.getName())
                            .lore(
                                    ability.getDescription(),
                                    "",
                                    inOtherSlot ? "&cAlready in " + (isPrimary ? "Secondary" : "Primary") + " slot" : (selected ? "&aCurrently selected" : "&eClick to select")
                            )
                            .setGlow(selected)
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    // Check if ability is already in the other slot
                    KitAbility otherSlotAbility = isPrimary ? kit.getSecondaryAbility() : kit.getPrimaryAbility();
                    if (otherSlotAbility != null && otherSlotAbility.getName().equals(ability.getName())) {
                        sendMessage(player, "&cThis ability is already in the " + (isPrimary ? "Secondary" : "Primary") + " slot!");
                        playFail(player);
                        return;
                    }
                    
                    if (isPrimary) {
                        kit.setPrimaryAbility(ability);
                    } else {
                        kit.setSecondaryAbility(ability);
                    }
                    saveKit();
                    sendMessage(player, "&a" + (isPrimary ? "Primary" : "Secondary") + " ability set to: " + ability.getName());
                    playSuccess(player);
                    new KitAbilitySlotSelectMenu(kit, player).open();
                }
            });
            slot++;
        }

        buttons.put(4, new BackButton(new KitAbilitySlotSelectMenu(kit, player)));

        // Fill row
        Button filler = getPlaceholderButton();
        for (int i = 0; i < 9; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, filler);
            }
        }

        return buttons;
    }

    private void saveKit() {
        kitStorage.saveKit(kit);
    }
}
