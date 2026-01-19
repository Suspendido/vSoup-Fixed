package kami.gg.souppvp.events.impl.tnttag.task;

import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.impl.tnttag.TNTTagState;
import kami.gg.souppvp.events.impl.tnttag.player.TNTGamePlayer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TNTTagMatchStartTask extends TNTTagTask {

    public TNTTagMatchStartTask(TNTTagGame game) {
        super(game, TNTTagState.STARTING);
    }

    @Override
    public void onRun() {
        TNTTagGame game = getGame();

        if (getTicks() >= 3) {
            startRound(game);
            return;
        }

        countdownTick(game);
    }

    private void startRound(TNTTagGame game) {
        game.broadcastMessage(CC.translate("&bMatch Started!"));
        game.setState(TNTTagState.RUNNING);
        game.setRoundStartTime(System.currentTimeMillis());
        game.pickNewTNT();

        game.setEventTask(new TNTTagRunningTask(game));

        for (TNTGamePlayer gamePlayer : game.getEventPlayers().values()) {
            Player player = Bukkit.getPlayer(gamePlayer.getUuid());
            if (player != null && player.isOnline()) {
                playStartSound(player);
                PlayerUtil.allowMovement(player);
            }
        }
    }

    private void countdownTick(TNTTagGame game) {
        int seconds = getRemainingSeconds(3);
        game.broadcastMessage("&7The match will be starting in &b" + seconds + "&7...");

        for (TNTGamePlayer gamePlayer : game.getEventPlayers().values()) {
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