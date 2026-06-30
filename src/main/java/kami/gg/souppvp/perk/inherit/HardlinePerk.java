package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HardlinePerk extends Perk {

    @Override
    public String getName() {
        return "Hardline";
    }

    @Override
    public String getColor() {
        return "&4";
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Require 1 less kill for your killstreaks.", "&c&lNOTE: &7Resets your current killstreak when removed.");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.REDSTONE_BLOCK);
    }

    @Override
    public int getCost() {
        return 1000;
    }

}
