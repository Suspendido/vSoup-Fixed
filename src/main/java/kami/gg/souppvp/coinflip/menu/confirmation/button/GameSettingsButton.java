package kami.gg.souppvp.coinflip.menu.confirmation.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlipState;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GameSettingsButton extends Button {

    private final int amount;

    public GameSettingsButton(int amount) {
        this.amount = amount;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&e&lWager");
        lore.add("&a" + amount + " credits");
        lore.add("");
        lore.add("&7Click to &b&lEDIT &7wager amount!");
        return new ItemBuilder(Material.BOOK_AND_QUILL)
                .name("&b&lGame Settings")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setCoinFlipState(CoinFlipState.CREATING);
        sendMessage(player, "&7Type an integer amount to set your &e&lwager &7amount.");
        sendMessage(player, "&7To &c&lcancel&7, type anything else.");
        player.closeInventory();
    }
}
