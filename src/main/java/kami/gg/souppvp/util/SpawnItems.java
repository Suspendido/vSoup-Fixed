package kami.gg.souppvp.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SpawnItems {

    public static ItemStack KITS_SELECTOR = new ItemBuilder(Material.DIAMOND_SWORD)
            .name("&bKits Selector &7(Right Click)")
            .build();

    public static ItemStack HOST_EVENTS = new ItemBuilder(Material.DIAMOND)
            .name("&bHost Events &7(Right Click)")
            .build();

    public static ItemStack SHOP = new ItemBuilder(Material.CHEST)
            .name("&bShop &7(Right Click)")
            .build();

    public static ItemStack YOUR_STATISTICS = new ItemBuilder(Material.SKULL_ITEM)
            .name("&bYour Statistics &7(Right Click)")
            .durability(3)
            .build();

    public static ItemStack PREVIOUS_KIT = new ItemBuilder(Material.WATCH)
            .name("&bSelect Previous Kit &7(Right Click)")
            .build();

    public static ItemStack YOUR_OPTIONS = new ItemBuilder(Material.NETHER_STAR)
            .name("&bConfigure Options &7(Right Click)")
            .build();

}
