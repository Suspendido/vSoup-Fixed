package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.perk.Perk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RevengePerk extends Perk {

    @Override
    public String getName() {
        return "Revenge";
    }

    @Override
    public String getColor() {
        return "&4";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Gain triple your normal credits for killing", "&7a player who just killed you.");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.FERMENTED_SPIDER_EYE);
    }

    @Override
    public int getCost() {
        return 850;
    }

}
