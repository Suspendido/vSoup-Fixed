package kami.gg.souppvp.tier.button;

import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.tier.menu.TierIconSelectorMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TierIconSelectorButton extends Button {

    private final Profile profile;

    public TierIconSelectorButton(Profile profile) {
        this.profile = profile;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        TierCategory currentCategory = TierCategory.getCategoryByName(profile.getSelectedTierIcon());
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&7Current Icon: " + currentCategory.getFormattedIcon());
        lore.add("");
        lore.add("&eClick to change your tier icon!");

        return new ItemBuilder(Material.PAINTING)
                .name("&dTier Icon Selector")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        new TierIconSelectorMenu(player).open();
        playNeutral(player);
    }
}
