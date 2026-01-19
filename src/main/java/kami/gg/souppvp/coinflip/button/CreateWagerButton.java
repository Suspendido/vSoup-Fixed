package kami.gg.souppvp.coinflip.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlipState;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.TaskUtil;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CreateWagerButton extends Button {

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (SoupPvP.getInstance().getCoinFlipsHandler().hasCoinFlipWager(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cYou already have a coin flip wager available."));
            playFail(player);
            return;
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setCoinFlipState(CoinFlipState.CREATING);
        playNeutral(player);
        TaskUtil.runLater(player::closeInventory, 1L);
        sendMessage(player, "&7Type an integer amount to set your &e&lwager &7amount.");
        sendMessage(player, "&7To &c&lcancel&7, type anything else.");
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();

        lore.add(CC.MENU_BAR);
        lore.add("&7Create to create your own custom game!");
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(Material.BOOK_AND_QUILL)
                .name("&b&lCreate Game")
                .lore(lore)
                .build();
    }

}
