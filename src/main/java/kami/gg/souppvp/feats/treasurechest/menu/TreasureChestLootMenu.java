package kami.gg.souppvp.feats.treasurechest.menu;

import kami.gg.souppvp.feats.treasurechest.TreasureChest;
import kami.gg.souppvp.feats.treasurechest.reward.TreasureChestReward;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import kami.gg.souppvp.util.menu.button.BackButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (c) 2026. @Comunidad, made since 1/7/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class TreasureChestLootMenu extends Menu {

    private final TreasureChest treasureChest;

    public TreasureChestLootMenu(Player player, TreasureChest treasureChest) {
        super(player, "Loot", 36, true);
        this.treasureChest = treasureChest;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> toReturn = new HashMap<>();
        int currentSlot = 9;

        for (TreasureChestReward reward : treasureChest.getRewards()) {
            if (reward.getItemStack() != null) {
                final int finalSlot = currentSlot;
                toReturn.put(finalSlot, new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return reward.getItemStack().clone();
                    }
                });
                currentSlot++;
            }
        }

        // Fill row
        Button filler = getPlaceholderButton();
        for (int i = 0; i < 9; i++) {
            if (!toReturn.containsKey(i)) {
                toReturn.put(i, filler);
            }
        }

        toReturn.put(4, new BackButton(new TreasureChestMenu(player)));

        return toReturn;
    }
}
