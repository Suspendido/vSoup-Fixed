package kami.gg.souppvp.coinflip.menu.animation.sub;

import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GreenMenu extends Menu {

    private final CoinFlip coinFlip;

    public GreenMenu(CoinFlip coinFlip) {
        this.coinFlip = coinFlip;
        setPlaceholder(true);
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return "&a&lCoinflip Match";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Player creator = Bukkit.getPlayer(coinFlip.getCreator());
        String name = creator != null ? creator.getName() : "Unknown";

        Button skullButton = new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.SKULL_ITEM)
                        .data(3)
                        .name("&a&l" + name)
                        .setSkullOwner(name)
                        .lore(Collections.singletonList(" "))
                        .build();
            }
        };

        int[] decorationSlots = {
                3, 4, 5,
                12, 13, 14,
                21, 22, 23
        };

        for (int slot : decorationSlots) {
            buttons.put(slot, skullButton);
        }

        return buttons;
    }
}