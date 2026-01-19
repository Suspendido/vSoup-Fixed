package kami.gg.souppvp.coinflip.menu.confirmation.button;

import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CancelSettingsButton extends Button {

    private int amount;

    public CancelSettingsButton(int amount) {
        this.amount = amount;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);
        lore.add("&7Click to &c&lCANCEL &7and &c&lCLOSE &7the game menu!");
        lore.add(CC.MENU_BAR);
        return new ItemBuilder(Material.INK_SACK)
                .name("&c&lCancel Custom Game")
                .lore(lore)
                .durability(1)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.isLeftClick()){
            player.closeInventory();
        }
    }
}
