package kami.gg.souppvp.feats.treasurechest.reward;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter @Setter
@AllArgsConstructor
public class TreasureChestReward {

    private ItemStack itemStack;
    private double chance;
    private String command;
    private boolean grantItem;
    private boolean broadcast;

}
