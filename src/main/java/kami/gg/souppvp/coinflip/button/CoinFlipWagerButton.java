package kami.gg.souppvp.coinflip.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.coinflip.events.WagerCancelEvent;
import kami.gg.souppvp.coinflip.events.WagerStartEvent;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CoinFlipWagerButton extends Button {

    private final CoinFlip coinFlip;

    public CoinFlipWagerButton(CoinFlip coinFlip) {
        this.coinFlip = coinFlip;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile creatorProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(coinFlip.getCreator());
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Player creatorPlayer = Bukkit.getPlayer(coinFlip.getCreator());
        String creatorName = creatorPlayer != null ? creatorPlayer.getName() : "Unknown";
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add("&e&lWager:");
        lore.add("&a" + coinFlip.getAmount() + " credits");
        lore.add("");
        lore.add("&e&l" + creatorName + "'s Stats");
        lore.add("&c• &eTotal Games: &a" + creatorProfile.getTotalWagerGames());
        lore.add("&c• &eWon: &a" + creatorProfile.getWagersWon());
        lore.add("&c• &eLost: &a" + creatorProfile.getWagersLost());

        int totalGames = creatorProfile.getTotalWagerGames();
        if (totalGames == 0) {
            lore.add("&c• &eWin Percent: &aN/A");
        } else {
            lore.add("&c• &eWin Percent: &a" + creatorProfile.getWinPercent() + "%");
        }

        lore.add("");

        if (profile.equals(creatorProfile)) {
            lore.add("&7Right-Click to &c&lCANCEL &7the bet!");
        } else if (profile.getCredits() < coinFlip.getAmount()) {
            lore.add("&cInsufficient Credits!");
        } else {
            lore.add("&7Click here to &a&lACCEPT &7the bet!");
        }

        return new ItemBuilder(Material.SKULL_ITEM)
                .durability((short) 3)
                .setSkullOwner(creatorName)
                .name("&a&l" + creatorName)
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile creator = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(coinFlip.getCreator());
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (creator.equals(profile)) {
            if (clickType.isLeftClick()) {
                sendMessage(player, "&cYou cannot accept your own coin flip game.");
                return;
            }

            if (clickType.isRightClick()) {
                sendMessage(player, "&7The wager has been returned. (&a" + coinFlip.getAmount() + " &acredits&7)");
                sendMessage(player, "&cYou cancelled your coinflip game.");

                Bukkit.getPluginManager().callEvent(new WagerCancelEvent(coinFlip));
                PlayerUtil.playSound(player, Sound.CLICK);
            }
            return;
        }

        if (!clickType.isLeftClick()) return;

        if (profile.getCredits() < coinFlip.getAmount()) {
            PlayerUtil.playSound(player, Sound.DIG_GRASS);
            return;
        }

        if (coinFlip.getOpponent() != null) {
            PlayerUtil.playSound(player, Sound.DIG_GRASS);
            return;
        }

        if (SoupPvP.getInstance().getCoinFlipsHandler().hasCoinFlipWager(profile.getUuid())) {
            CoinFlip existing = SoupPvP.getInstance().getCoinFlipsHandler().getPlayerCoinFlip(player.getUniqueId());
            SoupPvP.getInstance().getCoinFlipsHandler().removeCoinFlip(existing);
        }

        Bukkit.getPluginManager().callEvent(new WagerStartEvent(coinFlip, player.getUniqueId()));
        PlayerUtil.playSound(player, Sound.CLICK);
    }

}
