package kami.gg.souppvp.killstreak.special.impl;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialTypeKillstreak;
import kami.gg.souppvp.killstreak.KillstreakReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GrandmaSoupsKillstreak implements SpecialKillstreak {

    @Override
    public SpecialTypeKillstreak getSpecialType() {
        return SpecialTypeKillstreak.GRANDMA_SOUPS;
    }

    @Override
    public void apply(Player player, ConfigurableKillstreak.RewardData rewardData) {
        for (int i = 0; i < 2; i++) {
            giveSoup(player);
        }
    }

    private void giveSoup(Player player) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(KillstreakReward.GRANDMA_SOUP);
            return;
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            if (item.getType() == Material.BOWL || item.getType() == Material.MUSHROOM_SOUP) {
                player.getInventory().setItem(i, KillstreakReward.GRANDMA_SOUP);
                return;
            }
        }
    }
}
