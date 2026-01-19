package kami.gg.souppvp.coinflip.menu.confirmation.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.events.WagerCreateEvent;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConfirmSettingsButton extends Button {

    private final int amount;

    public ConfirmSettingsButton(int amount) {
        this.amount = amount;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(CC.MENU_BAR);
        lore.add("&7Click to &a&lCONFIRM &7and &a&lPOST &7coinflip game!");
        lore.add(CC.MENU_BAR);
        return new ItemBuilder(Material.INK_SACK)
                .name("&a&lConfirm Settings")
                .lore(lore)
                .durability(10)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        WagerCreateEvent wagerCreateEvent = new WagerCreateEvent(player.getUniqueId(), amount);
        Bukkit.getPluginManager().callEvent(wagerCreateEvent);
        profile.setCredits(profile.getCredits() - amount);
        sendMessage(player, "&7The wager has been collected. (&a" + amount + " &acredits&7)");
        broadcast(
                "",
                "&b" + player.getDisplayName() + " &fhas made a CoinFlip game for &b" + amount + " credits&f!",
                ""
        );
        player.closeInventory();
    }
}
