package kami.gg.souppvp.shop.items.button;

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

public class SoupRefillButton extends Button {

    private final int costCredits;

    public SoupRefillButton(int costCredits) {
        this.costCredits = costCredits;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();

        lore.add("&7Refills all your empty slots with soup.");
        lore.add("");
        lore.add("&fPrice: &b" + costCredits);
        lore.add("");
        lore.add(profile.getCredits() >= costCredits
                ? "&eClick to purchase!"
                : "&cInsufficient Credits!");

        return new ItemBuilder(Material.MUSHROOM_SOUP)
                .name("&bSoup Refill")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            PlayerUtil.playSound(player, Sound.DIG_GRASS, 1.0);
            sendMessage(player, "&cYou can't do this in spawn.");
            return;
        }

        if (profile.getCredits() < costCredits) {
            PlayerUtil.playSound(player, Sound.DIG_GRASS, 1.0);
            sendMessage(player, "&cInsufficient credits.");
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            playFail(player);
            sendMessage(player, "&cYour inventory is full!");
            return;
        }

        sendMessage(player, "&aSuccessfully bought the &bSoup Refill&a.");
        profile.setCredits(profile.getCredits() - costCredits);
        PlayerUtil.giveSoup(player);
        playSuccess(player);
    }
}
