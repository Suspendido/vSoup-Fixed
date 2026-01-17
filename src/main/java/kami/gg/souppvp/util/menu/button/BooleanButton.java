package kami.gg.souppvp.util.menu.button;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

import kami.gg.souppvp.util.Callback;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BooleanButton extends Button {

    private final boolean confirm;
    private final Callback<Boolean> callback;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add("");

        if (confirm) {
            lore.add("&aClick here to confirm");
            lore.add("&athe procedure action.");
        } else {
            lore.add("&cClick here to cancel");
            lore.add("&cthe procedure action.");
        }
        return new ItemBuilder(Material.WOOL)
                .name(confirm ? "&a&lConfirm" : "&c&lCancel")
                .lore(lore)
                .data(confirm ? 5 : 14)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (confirm) {
            playSuccess(player);
        } else {
            playFail(player);
        }
        player.closeInventory();
        callback.callback(confirm);
    }

    @ConstructorProperties(value={"confirm", "callback"})
    public BooleanButton(boolean confirm, Callback<Boolean> callback) {
        this.confirm = confirm;
        this.callback = callback;
    }
}

