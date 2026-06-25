package kami.gg.souppvp.kit.button;

import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.menu.editor.KitEditorMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitEditButton extends Button {

    private final Kit kit;

    public KitEditButton(Kit kit) {
        this.kit = kit;
    }

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
        lore.add("");
        lore.add("&eClick to edit this kit.");

        return new ItemBuilder(kit.getIcon())
                .name(kit.getRarityType().getColor() + kit.getName())
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        new KitEditorMenu(kit, player).open();
    }
}
