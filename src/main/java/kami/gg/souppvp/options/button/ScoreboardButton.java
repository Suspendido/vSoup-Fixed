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

public class ScoreboardButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add("&fEdit the visibility of the the scoreboard.");
        lore.add("");
        if (profile.getEnableScoreboard()) {
            lore.add("&7▸  &aEnabled");
            lore.add("&f  &cDisabled");
        }  else {
            lore.add("&f  &cEnabled");
            lore.add("&7▸  &aDisabled");
        }
        lore.add("");
        lore.add("&eClick to toggle!");
        return new ItemBuilder(Material.ITEM_FRAME)
                .name("&bScoreboard Visibility")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.isLeftClick()){
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            profile.setEnableScoreboard(!profile.getEnableScoreboard());
            PlayerUtil.playSound(player, Sound.CLICK, 1.0);
        }
    }

}
