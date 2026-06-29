package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ProAbility implements KitAbility {

    @Override
    public String getName() {
        return "Pro";
    }

    @Override
    public String getDescription() {
        return "&fEarn double credits on every kill";
    }

    @Override
    public String getColor() {
        return "&b";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.DIAMOND).lore("Dont Display").build();
    }
}
