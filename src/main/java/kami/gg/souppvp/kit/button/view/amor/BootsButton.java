package kami.gg.souppvp.kit.button.view.amor;

import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class BootsButton extends Button {

    private final Kit kit;

    public BootsButton(Kit kit){
        this.kit = kit;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<ItemStack> list = Arrays.asList(kit.getArmor());
        return list.getFirst();
    }

}
