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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class KitEffectsEditMenu extends Menu {

    private final Kit kit;
    private final KitStorage kitStorage;

    public KitEffectsEditMenu(Kit kit, Player player) {
        super(player, "Edit Potion Effects", 36, false);
        this.kit = kit;
        this.kitStorage = SoupPvP.getInstance().getKitStorage();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = 9;
        for (int i = 0; i < kit.getPotionEffects().size(); i++) {
            if (slot >= 35) break;
            
            final int index = i;
            PotionEffect effect = kit.getPotionEffects().get(i);
            
            buttons.put(slot, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.POTION)
                            .name("&b" + effect.getType().getName())
                            .lore(
                                    "&b┃ &fAmplifier: &f" + (effect.getAmplifier() + 1),
                                    "&b┃ &fDuration: &f" + (effect.getDuration() == Integer.MAX_VALUE ? "Permanent" : effect.getDuration() + " ticks"),
                                    "",
                                    "&cClick to remove"
                            )
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                    PotionEffect removed = kit.getPotionEffects().remove(index);
                    saveKit();
                    playFail(player);
                    player.sendMessage(CC.t("&aRemoved effect: " + removed.getType().getName()));
                    update();
                }
            });
            slot++;
            if (slot == 17 || slot == 26) slot += 2;
        }

        buttons.put(3, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BREWING_STAND_ITEM)
                        .name("&aAdd Effect")
                        .lore(
                                "&fClick to add a new",
                                "&fpotion effect"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KitEffectTypeSelectMenu(kit, player).open();
            }
        });

        buttons.put(4, new BackButton(new KitEditorMenu(kit, player)));

        buttons.put(5, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BARRIER)
                        .name("&c&lClear All Effects")
                        .lore(
                                "&cClick to clear all",
                                "&cpotion effects"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                kit.getPotionEffects().clear();
                saveKit();
                player.sendMessage(CC.t("&aAll effects cleared!"));
                playFail(player);
                update();
            }
        });

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
