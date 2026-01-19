package kami.gg.souppvp.coinflip.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.coinflip.events.WagerCancelEvent;
import kami.gg.souppvp.coinflip.events.WagerCreateEvent;
import kami.gg.souppvp.coinflip.events.WagerStartEvent;
import kami.gg.souppvp.coinflip.menu.animation.AnimatedMenu;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class WagerCustomEventListeners implements Listener {

    private final SoupPvP plugin = SoupPvP.getInstance();

    @EventHandler
    public void onWagerCreate(WagerCreateEvent event) {
        new CoinFlip(event.getCreator(), event.getAmount());
    }

    @EventHandler
    public void onWagerCancel(WagerCancelEvent event) {
        CoinFlip coinFlip = event.getCoinFlip();
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(coinFlip.getCreator());

        if (profile != null) {
            profile.setCredits(profile.getCredits() + coinFlip.getAmount());
        }

        plugin.getCoinFlipsHandler().removeCoinFlip(coinFlip);
    }

    @EventHandler
    public void onWagerStart(WagerStartEvent event) {
        CoinFlip coinFlip = event.getCoinFlip();
        UUID opponentUUID = event.getOpponent();

        coinFlip.setOpponent(opponentUUID);

        Player creator = Bukkit.getPlayer(coinFlip.getCreator());
        Player opponent = Bukkit.getPlayer(opponentUUID);

        if (creator != null) {
            new AnimatedMenu(coinFlip).openMenu(creator);
        }

        if (opponent != null) {
            new AnimatedMenu(coinFlip).openMenu(opponent);
        }
    }
}