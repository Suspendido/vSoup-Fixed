package kami.gg.souppvp.shop.items.button;

import kami.gg.souppvp.shop.bartender.BartenderMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BartenderButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add("&7Preview available combat potions!");
        lore.add("");
        lore.add("&eClick to view!");
        return new ItemBuilder(Material.BREWING_STAND_ITEM).name("&bBartender").lore(lore).build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        playNeutral(player);
        new BartenderMenu(player).open();
    }

}
