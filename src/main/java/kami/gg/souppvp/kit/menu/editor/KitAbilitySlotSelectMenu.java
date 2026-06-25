package kami.gg.souppvp.kit.menu.editor;

import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.ability.KitAbility;
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

public class KitAbilitySlotSelectMenu extends Menu {

    private final Kit kit;

    public KitAbilitySlotSelectMenu(Kit kit, Player player) {
        super(player, "Select Ability Slot", 27, false);
        this.kit = kit;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        KitAbility primaryAbility = kit.getPrimaryAbility();
        KitAbility secondaryAbility = kit.getSecondaryAbility();

        buttons.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BLAZE_POWDER)
                        .name("&b&lPrimary Ability")
                        .lore(
                                "&b┃ &fCurrent: " + (primaryAbility != null ? primaryAbility.getColor() + primaryAbility.getName() : "&cNone"),
                                "",
                                "&aClick to edit primary ability"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KitAbilitySelectMenu(kit, true, player).open();
            }
        });

        buttons.put(15, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.GLOWSTONE_DUST)
                        .name("&b&lSecondary Ability")
                        .lore(
                                "&b┃ &fCurrent: " + (secondaryAbility != null ? secondaryAbility.getColor() + secondaryAbility.getName() : "&cNone"),
                                "",
                                "&aClick to edit secondary ability"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                playNeutral(player);
                new KitAbilitySelectMenu(kit, false, player).open();
            }
        });

        buttons.put(22, new BackButton(new KitEditorMenu(kit, player)));

        return buttons;
    }
}
