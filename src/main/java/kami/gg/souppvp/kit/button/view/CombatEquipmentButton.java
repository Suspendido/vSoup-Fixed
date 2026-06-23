package kami.gg.souppvp.kit.button.view;

import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CombatEquipmentButton extends Button {

    private final Kit kit;
    private final int position;

    public CombatEquipmentButton(Kit kit, int position) {
        this.kit = kit;
        this.position = position;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return kit.getCombatEquipments().get(position);
    }
}
