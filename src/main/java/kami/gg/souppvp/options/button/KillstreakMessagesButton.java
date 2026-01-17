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

public class KillstreakMessagesButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add("&fEdit the visibility of the killstreak messages.");
        lore.add("");
        if (profile.getEnableKillstreakMessages()) {
            lore.add("&7▸  &aEnabled");
            lore.add("&f  &cDisabled");
        }  else {
            lore.add("&f  &cEnabled");
            lore.add("&7▸  &aDisabled");
        }
        lore.add("");
        lore.add("&eClick to toggle!");

        return new ItemBuilder(Material.EXPLOSIVE_MINECART)
                .name("&bKillstreak Messages")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setEnableKillstreakMessages(!profile.getEnableKillstreakMessages());
        PlayerUtil.playSound(player, Sound.CLICK);
    }
}
