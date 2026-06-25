package kami.gg.souppvp.kit.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public interface KitAbility extends Listener {

    String getName();
    String getDescription();
    String getColor();
    ItemStack getItem();

    default void onKitSelect(Player player) {
    }

    default void onKitDeselect(Player player) {
    }
}
