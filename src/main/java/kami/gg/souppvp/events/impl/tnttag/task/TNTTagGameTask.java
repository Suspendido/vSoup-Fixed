package kami.gg.souppvp.events.impl.tnttag.task;

import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.events.util.EventGamePlayer;
import kami.gg.souppvp.events.util.task.EventTask;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TNTTagGameTask extends EventTask {

    private static final int START_DELAY_SECONDS = 3;

    public TNTTagGameTask(TNTTagGame game) {
        super(game, EventType.TNTTAG, EventState.STARTING);
    }

    @Override
    protected void onRun() {
        TNTTagGame game = (TNTTagGame) event;

        if (game.getState() == EventState.STARTING) {
            handleStarting(game);
        } else if (game.getState() == EventState.RUNNING) {
            handleRunning(game);
        }
    }

    private void handleStarting(TNTTagGame game) {
        if (getTicks() >= START_DELAY_SECONDS) {
            startRound(game);
            return;
        }

        countdownTick(game);
    }

    private void startRound(TNTTagGame game) {
        game.broadcastMessage(CC.t("&bMatch Started!"));
        game.setState(EventState.RUNNING);
        game.setRoundStartTime(System.currentTimeMillis());
        game.pickNewTNT();

        for (EventGamePlayer gamePlayer : game.getEventPlayers().values()) {
            Player player = Bukkit.getPlayer(gamePlayer.getUuid());
            if (player != null && player.isOnline()) {
                playStartSound(player);
                PlayerUtil.allowMovement(player);
            }
        }
    }

    private void handleRunning(TNTTagGame game) {
        int remaining = game.getTimeRemaining();

        if (remaining <= 0) {
            game.explode();
        }
    }

    private void countdownTick(TNTTagGame game) {
        int seconds = getRemainingSeconds(START_DELAY_SECONDS);
        game.broadcastMessage("&7The match will be starting in &b" + seconds + "&7...");

        for (EventGamePlayer gamePlayer : game.getEventPlayers().values()) {
            Player player = Bukkit.getPlayer(gamePlayer.getUuid());
            if (player != null && player.isOnline()) {
                playCountdownSound(player);
            }
        }
    }

    private void playCountdownSound(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
    }

    private void playStartSound(Player player) {
        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.0F, 1.0F);
    }
}
