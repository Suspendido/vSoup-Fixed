package kami.gg.souppvp.profile.menus.options.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KillDeathMessagesButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add("&fEdit the visibility of the kills/deaths messages.");
        lore.add("");
        if (profile.getEnableKillDeathMessages()){
            lore.add("&7▸  &aEnabled");
            lore.add("&f  &cDisabled");
        }  else {
            lore.add("&f  &cEnabled");
            lore.add("&7▸  &aDisabled");
        }
        lore.add("");
        lore.add("&eClick to toggle!");

        return new ItemBuilder(Material.BLAZE_POWDER)
                .name("&bKill/Death Messages")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setEnableKillDeathMessages(!profile.getEnableKillDeathMessages());
        playSuccess(player);
    }
}
