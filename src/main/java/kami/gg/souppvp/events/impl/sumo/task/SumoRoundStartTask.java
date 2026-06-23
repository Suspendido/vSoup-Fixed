package kami.gg.souppvp.events.impl.sumo.task;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.impl.sumo.SumoState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SumoRoundStartTask extends SumoTask {

    private static final int START_DELAY_SECONDS = 3;

    public SumoRoundStartTask(Sumo sumo) {
        super(sumo, SumoState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        Sumo sumo = getSumo();

        Player playerA = sumo.getRoundPlayerA().getPlayer();
        Player playerB = sumo.getRoundPlayerB().getPlayer();

        Location spawnA = SoupPvP.getInstance().getSumoHandler().getSpawnA();
        Location spawnB = SoupPvP.getInstance().getSumoHandler().getSpawnB();

        // Always keep players in position during countdown
        teleportPlayers(playerA, playerB, spawnA, spawnB);

        if (getTicks() >= START_DELAY_SECONDS) {
            startRound(sumo, playerA, playerB);
            return;
        }

        countdownTick(sumo, playerA, playerB);
    }

    private void startRound(Sumo sumo, Player playerA, Player playerB) {
        sumo.broadcastMessage(CC.t("&bMatch Started!"));
        sumo.setEventTask(null);
        sumo.setState(SumoState.ROUND_FIGHTING);
        sumo.setRoundStart(System.currentTimeMillis());

        playStartSound(playerA);
        playStartSound(playerB);

        PlayerUtil.allowMovement(playerA);
        PlayerUtil.allowMovement(playerB);
    }

    private void countdownTick(Sumo sumo, Player playerA, Player playerB) {
        int seconds = getRemainingSeconds(3);

        playCountdownSound(playerA);
        playCountdownSound(playerB);

        PlayerUtil.denyMovement(playerA);
        PlayerUtil.denyMovement(playerB);

        sumo.broadcastMessage("&7The round will be starting in &b" + seconds + "&7...");
    }

    private void teleportPlayers(Player a, Player b, Location spawnA, Location spawnB) {
        a.teleport(spawnA);
        b.teleport(spawnB);
    }

    private void playCountdownSound(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
    }

    private void playStartSound(Player player) {
        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.0F, 1.0F);
    }
}
