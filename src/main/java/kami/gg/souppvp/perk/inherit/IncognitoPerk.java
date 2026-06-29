package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class IncognitoPerk extends Perk {

    @Override
    public String getName() {
        return "Incognito";
    }

    @Override
    public String getColor() {
        return "&8";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7Prevent your enemies from keeping"));
        lore.add(CC.t("&7track of your kill streak."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_SWORD);
    }

    @Override
    public int getCost() {
        return 450;
    }


}
