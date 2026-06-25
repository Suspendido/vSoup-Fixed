package kami.gg.souppvp.shop;

import kami.gg.souppvp.kit.menu.KitsBuyMenu;
import kami.gg.souppvp.shop.items.ItemsMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ShopMenu extends Menu {

    public ShopMenu(Player player) {
        super(player, "Server Shop", 27, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> button = new HashMap<>();

        button.put(12, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BEACON)
                        .name("&bItems Shop")
                        .build();
            }
            @Override
            public void clicked(Player player, ClickType clickType) {
                playNeutral(player);
                new ItemsMenu(player).open();
            }
        });

        button.put(14, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.DIAMOND_SWORD)
                        .name("&bKits Shop")
                        .build();
            }
            @Override
            public void clicked(Player player, ClickType clickType) {
                playNeutral(player);
                new KitsBuyMenu(player).open();
            }
        });
        setFillEnabled(true);
        return button;
    }
}
