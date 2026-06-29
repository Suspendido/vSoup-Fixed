package kami.gg.souppvp.perk;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Perk {

    public abstract String getName();
    public abstract String getColor();
    public abstract List<String> getDescription();
    public abstract ItemStack getIcon();
    public abstract int getCost();

}
