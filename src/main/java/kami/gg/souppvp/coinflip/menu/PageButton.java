package kami.gg.souppvp.coinflip.menu;

import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class PageButton extends Button {

    private final int mod;
    private final CoinFlipMenu menu;

    public void clicked(Player player, int i, ClickType clickType) {
        if (this.hasNext()) {
            this.menu.changePage(mod);
            Button.playNeutral(player);
        } else {
            Button.playFail(player);
        }

    }

    private boolean hasNext() {
        int pg = this.menu.getCurrentPage() + this.mod;
        return pg > 0 && this.menu.getTotalPages() >= pg;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.CARPET).name(this.mod > 0 ? "§a§lNext page" : "§7§lPrevious page").durability((byte) (this.mod > 0 ? 5 : 8)).build();
    }
}