package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TricksterPerk extends Perk {

    @Override
    public String getName() {
        return "Trickster";
    }

    @Override
    public String getColor() {
        return "&e";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7You will never display the correct", "&7health or bounty above your head.");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GOLD_INGOT);
    }

    @Override
    public int getCost() {
        return 350;
    }

}
