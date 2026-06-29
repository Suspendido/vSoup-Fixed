package kami.gg.souppvp.kit.ability;

import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2026. @Comunidad, made since 29/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class AbilityItemComparator {

    public static boolean isSameAbilityItem(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getType() != b.getType()) return false;

        if (a.hasItemMeta() && b.hasItemMeta()) {
            String nameA = a.getItemMeta().getDisplayName();
            String nameB = b.getItemMeta().getDisplayName();
            
            if (nameA != null && nameB != null) {
                return nameA.equals(nameB);
            }
        }

        // Compare durability for items that use it (INK_SACK, POTION, etc.)
        if (a.getDurability() != b.getDurability()) return false;

        return a.getType() == b.getType();
    }
}
