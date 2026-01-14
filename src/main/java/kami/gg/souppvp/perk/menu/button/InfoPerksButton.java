package kami.gg.souppvp.perk.menu.button;

import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InfoPerksButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.PAPER)
                .name("&bWhat are perks?")
                .lore(
                        "&7Each perk allows you to customize your playstyle to",
                        "&7benefit your combat skills!",
                        "&7You can select three perks, one from each tier level.",
                        "",
                        "&7&oPerks can only be purchased using in-game credits."
                ).build();
    }

}
