package kami.gg.souppvp.coinflip.button;

import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class WagerWinnerButton extends Button {

    private CoinFlip coinFlip;

    public WagerWinnerButton(CoinFlip coinFlip) {
        this.coinFlip = coinFlip;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();

        lore.add(CC.MENU_BAR);
        lore.add("&b&l" + Bukkit.getPlayer(coinFlip.getWinner()).getName() + "'s Won");
        lore.add("&b┃ &a+" + (coinFlip.getAmount() * 2) + " &fcredits");
        lore.add("");
        lore.add("&c&l" + Bukkit.getPlayer(coinFlip.getLoser()).getName() + "'s Lost");
        lore.add("&b┃ &c-" + coinFlip.getAmount() + " &fcredits");
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(Material.SKULL_ITEM)
                .data(3)
                .name("&a&l" + Bukkit.getPlayer(coinFlip.getWinner()).getName() + " WON!")
                .setSkullOwner(Bukkit.getPlayer(coinFlip.getWinner()).getName())
                .lore(lore)
                .build();
    }
}
