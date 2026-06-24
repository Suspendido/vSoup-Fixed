package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7There is a 50% chance you do not"));
        lore.add(CC.t("&7drop any soup on death."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.MUSHROOM_SOUP);
    }

    @Override
    public int getCost() {
        return 500;
    }

    @Override
    public int getTier() {
        return 3;
    }

}
