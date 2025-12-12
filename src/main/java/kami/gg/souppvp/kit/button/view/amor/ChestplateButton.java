package kami.gg.souppvp.kit.button.view.amor;

import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ChestplateButton extends Button {

    private final Kit kit;

    public ChestplateButton(Kit kit){
        this.kit = kit;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<ItemStack> list = Arrays.asList(kit.getArmor());
        return list.get(2);
    }

}
