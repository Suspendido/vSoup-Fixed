package kami.gg.souppvp.events.impl.tnttag.task;

import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.impl.tnttag.TNTTagState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.Cooldown;
import kami.gg.souppvp.util.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TNTTagStartTask extends TNTTagTask {

    private static final int FORCE_END_TICKS = 120;
    private static final int MIN_PLAYERS = 2;
    private static final int START_AFTER_TICKS = 30;
    private static final int ANNOUNCE_INTERVAL = 10;

    public TNTTagStartTask(TNTTagGame game) {
        super(game, TNTTagState.WAITING);
    }

    @Override
    public void onRun() {
        TNTTagGame game = getGame();
        int players = game.getRemainingPlayers().size();

        // Force end (IGUAL A SUMO)
        if (getTicks() >= FORCE_END_TICKS) {
            game.end();
            return;
        }

        // Not enough players anymore
        if (players <= 1 && game.getCooldown() != null) {
            game.setCooldown(null);
            game.broadcastMessage("&cThere are not enough players for the TNTTag Event to start.");
            return;
        }

        // Check if should start
        if (shouldStart(game, players)) {
            handleStartCountdown(game, players);
        }

        // Periodic announce
        if (getTicks() % ANNOUNCE_INTERVAL == 0) {
            game.announce();
        }
    }

    private boolean shouldStart(TNTTagGame game, int players) {
        return players == game.getMaxPlayers() || (getTicks() >= START_AFTER_TICKS && players >= MIN_PLAYERS);
    }

    private void handleStartCountdown(TNTTagGame game, int players) {
        if (game.getCooldown() == null) {
            startCooldown(game, players);
            return;
        }

        if (game.getCooldown().hasExpired()) {
            startEvent(game, players);
        }
    }

    private void startCooldown(TNTTagGame game, int players) {
        game.setCooldown(new Cooldown(11_000));

        FancyMessage message = new FancyMessage(
                CC.translate("&7The &4TNTTag &7Event will start in &b00:10&7! ")
        );

        message.then("[Click Here]")
                .color(ChatColor.GREEN)
                .command("/tnttag join")
                .tooltip(ChatColor.GREEN + "Click to join!")
                .then(" (" + players + "/" + game.getMaxPlayers() + ")")
                .color(ChatColor.WHITE);

        for (Player player : Bukkit.getOnlinePlayers()) {
            message.send(player);
        }
    }

    private void startEvent(TNTTagGame game, int players) {
        game.setTotalPlayers(players);
        game.broadcastMessage("&aThe TNTTag Event is starting!");
        game.onRound();
    }
}
