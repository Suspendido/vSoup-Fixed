package kami.gg.souppvp.perk.menu.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.perk.menu.PerkToggleMenu;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PerkToggleButton extends Button {

    private final Perk perk;

    public PerkToggleButton(Perk perk) {
        this.perk = perk;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        boolean disabled = SoupPvP.getInstance().getPerksHandler().isPerkDisabled(perk.getName());
        ItemStack icon = perk.getIcon() != null ? perk.getIcon() : new ItemStack(Material.BARRIER);

        return new ItemBuilder(icon)
                .name(perk.getColor() + perk.getName())
                .lore("&eEnabled: " + (disabled ? "&cFalse" : "&aTrue"))
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        boolean wasDisabled = SoupPvP.getInstance().getPerksHandler().isPerkDisabled(perk.getName());
        
        if (wasDisabled) {
            // Enable the perk
            SoupPvP.getInstance().getPerksHandler().setPerkDisabled(perk.getName(), false);
            sendMessage(player, "&aEnabled " + perk.getColor() + perk.getName() + "&a!");
            playSuccess(player);
        } else {
            // Disable the perk
            SoupPvP.getInstance().getPerksHandler().setPerkDisabled(perk.getName(), true);
            SoupPvP.getInstance().getPerksHandler().removeDisabledPerkFromAllPlayers(perk.getName());
            sendMessage(player, "&cDisabled " + perk.getColor() + perk.getName() + "&c!");
            playFail(player);
        }
        
        new PerkToggleMenu(player).open();
    }
}
