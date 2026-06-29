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
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Require 1 less kill for your killstreaks."));
        lore.add(CC.t("&c&lNOTE: &7Resets your current killstreak when removed."));
        return lore;
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
