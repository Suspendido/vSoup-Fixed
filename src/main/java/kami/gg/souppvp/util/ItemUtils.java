package kami.gg.souppvp.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUtils {

    public static ItemStack getMatItem(String string) {
        if (string == null || string.isEmpty()) {
            return new ItemStack(Material.REDSTONE_BLOCK);
        }

        if (string.contains(":")) {
            String[] split = string.split(":");
            Material mat = getMat(split[0]);
            ItemStack item = new ItemStack(mat);
            item.setDurability(Short.parseShort(split[1]));
            return item;
        }

        if (string.toLowerCase().startsWith("head_")) {
            String ownerName = string.substring(string.indexOf("_") + 1);
            ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) item.getItemMeta();

            if (meta != null) {
                meta.setOwner(ownerName);
                item.setItemMeta(meta);
            }

            return item;
        }

        return new ItemStack(getMat(string));
    }

    public static Material getMat(String string) {
        Material material = Material.matchMaterial(string);

        if (material == null) {
            Bukkit.getLogger().warning("[Azurite] Invalid Material: " + string + "! Returning REDSTONE_BLOCK as default!");
            return Material.REDSTONE_BLOCK;
        }

        return material;
    }

    public static String getName(ItemStack item) {
        if (item == null) {
            return "Unknown";
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }

        return item.getType().name().replace("_", " ").toLowerCase();
    }
}
