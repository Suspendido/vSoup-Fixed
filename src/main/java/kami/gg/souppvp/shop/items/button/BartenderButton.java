package kami.gg.souppvp.shop.items.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.shop.bartender.BartenderMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BartenderButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add(CC.translate("&7Preview available combat potions!"));
        lore.add("");
        lore.add(CC.translate("&eClick to view!"));
        return new ItemBuilder(Material.BREWING_STAND_ITEM).name(CC.translate("&bBartender")).lore(lore).build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.isLeftClick()){
            playNeutral(player);
            new BartenderMenu().openMenu(player);
        }
    }

}
