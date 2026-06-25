package kami.gg.souppvp.kit.ability.impl;

import kami.gg.souppvp.kit.ability.KitAbility;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NoneAbility implements KitAbility {

    @Override
    public String getName() {
        return "None";
    }

    @Override
    public String getDescription() {
        return "No special ability";
    }

    @Override
    public String getColor() {
        return "&c";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.BARRIER).build();
    }
}
