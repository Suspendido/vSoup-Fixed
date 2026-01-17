package kami.gg.souppvp.perk.menu.button;

import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InfoPerksButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.BOOK_AND_QUILL)
                .name("&b&lWhat are perks?")
                .lore(
                        "&fEach perk allows you to customize your playstyle to",
                        "&fbenefit your combat skills!",
                        "&fYou can select three perks, one from each tier level.",
                        "",
                        "&7&oPerks can only be purchased using in-game credits."
                ).build();
    }
}