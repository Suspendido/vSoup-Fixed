package kami.gg.souppvp.shop;

import kami.gg.souppvp.kit.menu.KitsBuyMenu;
import kami.gg.souppvp.perk.menu.PerksMenu;
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

    @Override
    public String getTitle(Player player) {
        return "Server Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player var1) {
        Map<Integer, Button> button = new HashMap<>();

        button.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BEACON)
                        .name("&bItems Shop")
                        .build();
            }
            @Override
            public void clicked(Player player, ClickType clickType) {
                playNeutral(player);
                new ItemsMenu().openMenu(player);
            }
        });

        button.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.ENDER_CHEST)
                        .name("&bPerks Shop")
                        .build();
            }
            @Override
            public void clicked(Player player, ClickType clickType) {
                playNeutral(player);
                new PerksMenu().openMenu(player);
            }
        });

        button.put(15, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.DIAMOND_SWORD)
                        .name("&bKits Shop")
                        .build();
            }
            @Override
            public void clicked(Player player, ClickType clickType) {
                playNeutral(player);
                new KitsBuyMenu().openMenu(player);
            }
        });
        setPlaceholder(true);
        return button;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 27;
    }
}
