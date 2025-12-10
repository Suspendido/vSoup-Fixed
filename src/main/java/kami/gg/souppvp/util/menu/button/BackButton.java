package kami.gg.souppvp.util.menu.button;

import java.util.Arrays;

import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class BackButton extends Button {

	private Menu back;

	@Override
	public ItemStack getButtonItem(Player player) {
		return new ItemBuilder(Material.REDSTONE)
				.name("&c&lBack")
				.lore(Arrays.asList(
						"&cClick here to return to",
						"&cthe previous menu.")
				)
				.build();
	}

	@Override
	public void clicked(Player player, ClickType clickType) {
		Button.playNeutral(player);
		back.openMenu(player);
	}

}
