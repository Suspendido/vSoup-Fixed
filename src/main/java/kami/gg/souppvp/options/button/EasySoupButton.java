package kami.gg.souppvp.options.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EasySoupButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add("&7This procedure will make that when");
        lore.add("&7when you soup you will not get a empty bowl.");
        lore.add("");
        if (profile.getEnableEasySoup()) {
            lore.add("&7▸  &aEnabled");
            lore.add("&f  &fDisabled");
        }  else {
            lore.add("&f  &fEnabled");
            lore.add("&7▸  &aDisabled");
        }
        lore.add("");
        lore.add("&eClick to toggle!");
        return new ItemBuilder(Material.MUSHROOM_SOUP)
                .name("&bEasy Soup")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.isLeftClick()) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            profile.setEnableEasySoup(!profile.getEnableEasySoup());
            PlayerUtil.playSound(player, Sound.CLICK);
        }
    }
}
