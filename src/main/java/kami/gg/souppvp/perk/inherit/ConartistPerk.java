package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.perk.Perk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConartistPerk extends Perk {

    @Override
    public String getName() {
        return "Conartist";
    }

    @Override
    public String getColor() {
        return "&6";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7There is a 50% chance you do not", "&7drop any soup on death.");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.MUSHROOM_SOUP);
    }

    @Override
    public int getCost() {
        return 500;
    }

}
