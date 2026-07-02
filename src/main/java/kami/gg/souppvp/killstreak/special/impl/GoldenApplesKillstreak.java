package kami.gg.souppvp.killstreak.special.impl;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialTypeKillstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GoldenApplesKillstreak implements SpecialKillstreak {

    @Override
    public SpecialTypeKillstreak getSpecialType() {
        return SpecialTypeKillstreak.GOLDEN_APPLES;
    }

    @Override
    public void apply(Player player, ConfigurableKillstreak.RewardData rewardData) {
        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, 8);
        giveItemReplacingSoup(player, goldenApple);
    }

    private void giveItemReplacingSoup(Player player, ItemStack reward) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(reward);
            return;
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            if (item.getType() == Material.BOWL || item.getType() == Material.MUSHROOM_SOUP) {
                player.getInventory().setItem(i, reward);
                return;
            }
        }
    }
}
