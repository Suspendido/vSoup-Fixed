package kami.gg.souppvp.coinflip;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.menu.wager.WagerResultMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.TaskUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class CoinFlip {

    private static final Random RANDOM = new Random();
    private final UUID creator;
    private final int amount;
    private UUID opponent;
    private UUID winner;
    private UUID loser;

    public CoinFlip(UUID creator, int amount) {
        this.creator = creator;
        this.amount = amount;

        SoupPvP.getInstance().getCoinFlipsHandler().getCoinFlips().add(this);
    }

    public void start() {
        SoupPvP plugin = SoupPvP.getInstance();
        Player creatorPlayer = Bukkit.getPlayer(creator);
        Player opponentPlayer = Bukkit.getPlayer(opponent);

        if (creatorPlayer == null || opponentPlayer == null) return;

        boolean creatorWins = RANDOM.nextBoolean();
        winner = creatorWins ? creator : opponent;
        loser  = creatorWins ? opponent : creator;

        Profile winnerProfile = plugin.getProfilesHandler().getProfileByUUID(winner);
        Profile loserProfile  = plugin.getProfilesHandler().getProfileByUUID(loser);

        winnerProfile.setCredits(winnerProfile.getCredits() + amount);
        winnerProfile.setTotalWagerGames(winnerProfile.getTotalWagerGames() + 1);
        winnerProfile.setWagersWon(winnerProfile.getWagersWon() + 1);

        loserProfile.setCredits(loserProfile.getCredits() - amount);
        loserProfile.setTotalWagerGames(loserProfile.getTotalWagerGames() + 1);
        loserProfile.setWagersLost(loserProfile.getWagersLost() + 1);

        TaskUtil.run(() -> {
            new WagerResultMenu(this).openMenu(Bukkit.getPlayer(winner));
            new WagerResultMenu(this).openMenu(Bukkit.getPlayer(loser));
        });

        Player nameWinner = Bukkit.getPlayer(winner);
        Player nameLosser = Bukkit.getPlayer(loser);

        List<String> message = CC.translate(Arrays.asList(
                "",
                "&b" + nameWinner.getDisplayName() + " &fbeat &a" + nameLosser.getDisplayName() + " &fin a coinflip for &b" + amount + " credits&f!",
                ""
        ));

        for (String s : message) {
            Bukkit.broadcastMessage(s);
        }

        nameWinner.sendMessage(CC.translate("&a&l+" + amount * 2 + (amount != 1 ? " credits" : " credit")));
        nameLosser.sendMessage(CC.translate("&c&l-" + amount + (amount != 1 ? " credits" : " credit")));

        TaskUtil.runLater(() -> {
            plugin.getCoinFlipsHandler().removeCoinFlip(this);
            plugin.getCoinFlipsHandler().getCoinFlips().remove(this);
        }, 10L);
    }
}