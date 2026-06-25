package kami.gg.souppvp.coinflip.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.coinflip.CoinFlipState;
import kami.gg.souppvp.coinflip.menu.confirmation.ConfirmWagerMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class CoinFlipListener implements Listener {

    private final SoupPvP plugin = SoupPvP.getInstance();

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile == null) return;
        if (profile.getCoinFlipState() != CoinFlipState.CREATING) return;

        event.setCancelled(true);
        String message = event.getMessage();

        if (!MathUtil.isNumeric(message)) {
            profile.setCoinFlipState(CoinFlipState.NONE);
            player.sendMessage(CC.t("&aSuccessfully cancelled the coin flip creation procedure."));
            return;
        }

        int amount = Integer.parseInt(message);

        if (amount <= 0) {
            player.sendMessage(CC.t("&cThe wager amount has to be greater than zero!"));
            return;
        }

        if (profile.getCredits() < amount) {
            player.sendMessage(CC.t("&cInsufficient credits! Try entering a lower amount!"));
            return;
        }

        profile.setCoinFlipState(CoinFlipState.NONE);
        new ConfirmWagerMenu(amount, player).update();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (!plugin.getCoinFlipsHandler().hasCoinFlipWager(uuid)) return;

        CoinFlip coinFlip = plugin.getCoinFlipsHandler().getPlayerCoinFlip(uuid);
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(uuid);

        if (profile != null) {
            profile.setCredits(profile.getCredits() + coinFlip.getAmount());
            profile.saveProfile();
        }

        plugin.getCoinFlipsHandler().removeCoinFlip(coinFlip);
    }
}