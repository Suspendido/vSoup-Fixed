package kami.gg.souppvp.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SpawnItems {

    public static ItemStack KITS_SELECTOR = new ItemBuilder(Material.ENCHANTED_BOOK)
            .name(CC.translate("&bKits Selector &7(Right Click)"))
            .build();

    public static ItemStack HOST_EVENTS = new ItemBuilder(Material.DIAMOND)
            .name(CC.translate("&bHost Events &7(Right Click)"))
            .build();

    public static ItemStack SHOP = new ItemBuilder(Material.CHEST)
            .name(CC.translate("&bShop &7(Right Click)"))
            .build();

    public static ItemStack YOUR_STATISTICS = new ItemBuilder(Material.SKULL_ITEM)
            .name(CC.translate("&bYour Statistics &7(Right Click)"))
            .durability(3)
            .build();

    public static ItemStack PREVIOUS_KIT = new ItemBuilder(Material.EMERALD)
            .name(CC.translate("&bSelect Previous Kit &7(Right Click)"))
            .build();

    public static ItemStack YOUR_OPTIONS = new ItemBuilder(Material.NETHER_STAR)
            .name(CC.translate("&bConfigure Options &7(Right Click)"))
            .build();

}
