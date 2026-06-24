package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.util.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BountyHunterPerk extends Perk {

    @Override
    public String getName() {
        return "Bounty Hunter";
    }

    @Override
    public String getColor() {
        return "&e";
    }

    @Override
    public List<String> getDescription() {
        List<String> lore = new ArrayList<>();
        lore.add(CC.t("&7See all bounties as red and bold,"));
        lore.add(CC.t("&7including trickters."));
        return lore;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GOLD_SWORD);
    }

    @Override
    public int getCost() {
        return 50;
    }

    @Override
    public int getTier() {
        return 1;
    }

}
