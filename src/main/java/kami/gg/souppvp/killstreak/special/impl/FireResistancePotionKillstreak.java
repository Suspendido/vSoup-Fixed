package kami.gg.souppvp.killstreak.special.impl;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialTypeKillstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class FireResistancePotionKillstreak implements SpecialKillstreak {

    @Override
    public SpecialTypeKillstreak getSpecialType() {
        return SpecialTypeKillstreak.FIRE_RESISTANCE_POTION;
    }

    @Override
    public void apply(Player player, ConfigurableKillstreak.RewardData rewardData) {
        ItemStack fireResistancePotion = new Potion(PotionType.FIRE_RESISTANCE, 1).toItemStack(1);
        giveItemReplacingSoup(player, fireResistancePotion);
    }

    private void giveItemReplacingSoup(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
            return;
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack current = player.getInventory().getItem(i);
            if (current == null) continue;

            if (current.getType() == Material.BOWL || current.getType() == Material.MUSHROOM_SOUP) {
                player.getInventory().setItem(i, item);
                return;
            }
        }
    }
}
