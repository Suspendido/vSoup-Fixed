package kami.gg.souppvp.shop.items;

import kami.gg.souppvp.shop.ShopMenu;
import kami.gg.souppvp.shop.items.button.*;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ItemsMenu extends Menu {

    public ItemsMenu(Player player) {
        super(player, "Select a feature to purchase", 27, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttonMap = new HashMap<>();
        buttonMap.put(10, new RepairDurabilityButton(50));
        buttonMap.put(11, new SoupRefillButton(100));
        buttonMap.put(12, new GoldenApplesButton(300));
        buttonMap.put(13, new MilkBucketButton(100));
        buttonMap.put(14, new BartenderButton());
        buttonMap.put(26, new BackButton(new ShopMenu(player)));
        setFillEnabled(true);
        return buttonMap;
    }

}
