package kami.gg.souppvp.kit;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum KitRarity {

    COMMON("Common", ChatColor.GRAY, 3000),
    UNCOMMON("Uncommon", ChatColor.GREEN, 3500),
    RARE("Rare", ChatColor.BLUE, 4000),
    ULTIMATE("Ultimate", ChatColor.AQUA,4500),
    LEGENDARY("Legendary", ChatColor.GOLD, 5000),
    MYTHICAL("Mythical", ChatColor.DARK_PURPLE, 5500);

    private final String name;
    private final ChatColor color;
    private final Integer price;

    KitRarity(String name, ChatColor color, Integer price) {
        this.name = name;
        this.color = color;
        this.price = price;
    }
}
