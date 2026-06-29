package kami.gg.souppvp.perk.inherit;

import kami.gg.souppvp.perk.Perk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
        return List.of("&7See all bounties as red and bold,", "&7including tricksters");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.GOLD_SWORD);
    }

    @Override
    public int getCost() {
        return 50;
    }

}
