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
        List<String> lore = new ArrayList<>();
        lore.add("&7You will never display the correct");
        lore.add("&7health or bounty above your head.");
        return CC.t(lore);
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
