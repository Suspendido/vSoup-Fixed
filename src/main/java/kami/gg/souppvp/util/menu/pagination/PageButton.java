package kami.gg.souppvp.util.menu.pagination;

import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PageButton extends Button {

	private int mod;
	private PaginatedMenu menu;

	@Override
	public ItemStack getButtonItem(Player player) {
		if (this.mod > 0) {
			if (hasNext(player)) {
				return new ItemBuilder(Material.CARPET)
                        .durability(5)
						.name("&aNext Page &8»")
						.build();
			} else {
				return new ItemBuilder(Material.CARPET)
                        .durability(8)
						.name("&7Next Page &c✖")
						.build();
			}
		} else {
			if (hasPrevious(player)) {
				return new ItemBuilder(Material.CARPET)
                        .durability(5)
						.name("&aPrevious Page &8«")
						.build();
			} else {
				return new ItemBuilder(Material.CARPET)
                        .durability(8)
						.name("&7Previous Page &c✖")
						.build();
			}
		}
	}

	@Override
	public void clicked(Player player, ClickType clickType) {
		if (this.mod > 0) {
			if (hasNext(player)) {
				this.menu.modPage(player, this.mod);
				Button.playNeutral(player);
			} else {
				Button.playFail(player);
			}
		} else {
			if (hasPrevious(player)) {
				this.menu.modPage(player, this.mod);
				Button.playNeutral(player);
			} else {
				Button.playFail(player);
			}
		}
	}

	private boolean hasNext(Player player) {
		int pg = this.menu.getPage() + this.mod;
		return this.menu.getPages(player) >= pg;
	}

	private boolean hasPrevious(Player player) {
		int pg = this.menu.getPage() + this.mod;
		return pg > 0;
	}

}
