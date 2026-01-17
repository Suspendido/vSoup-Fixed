package kami.gg.souppvp.tier.button;

import kami.gg.souppvp.tier.menu.TiersListMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ViewTiersButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.BOOK)
                .name("&aView All Tiers")
                .lore(
                        CC.MENU_BAR,
                        "&7Click to view all available tiers",
                        CC.MENU_BAR
                ).build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        playNeutral(player);
        new TiersListMenu().openMenu(player);
    }
}
