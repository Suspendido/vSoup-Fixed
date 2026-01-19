package kami.gg.souppvp.events.impl.sumo.task;

import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.impl.sumo.SumoState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.Cooldown;
import kami.gg.souppvp.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SumoStartTask extends SumoTask {

    private static final int FORCE_END_TICKS = 120;
    private static final int MIN_PLAYERS = 2;
    private static final int START_AFTER_TICKS = 30;
    private static final int ANNOUNCE_INTERVAL = 10;

    public SumoStartTask(Sumo sumo) {
        super(sumo, SumoState.WAITING);
    }

    @Override
    public void onRun() {
        Sumo sumo = getSumo();
        int players = sumo.getPlayers().size();

        // Force end
        if (getTicks() >= FORCE_END_TICKS) {
            sumo.end();
            return;
        }

        // Not enough players anymore
        if (players <= 1 && sumo.getCooldown() != null) {
            sumo.setCooldown(null);
            sumo.broadcastMessage("&cThere are not enough players for the Sumo Event to start.");
            return;
        }

        // Check if event should start
        if (shouldStart(sumo, players)) {
            handleStartCountdown(sumo, players);
        }

        // Periodic announce
        if (getTicks() % ANNOUNCE_INTERVAL == 0) {
            sumo.announce();
        }
    }

    private boolean shouldStart(Sumo sumo, int players) {
        return players == sumo.getMaxPlayers() || (getTicks() >= START_AFTER_TICKS && players >= MIN_PLAYERS);
    }

    private void handleStartCountdown(Sumo sumo, int players) {
        if (sumo.getCooldown() == null) {
            startCooldown(sumo, players);
            return;
        }

        if (sumo.getCooldown().hasExpired()) {
            startRound(sumo, players);
        }
    }

    private void startCooldown(Sumo sumo, int players) {
        sumo.setCooldown(new Cooldown(11_000));

        FancyMessage message = new FancyMessage(CC.translate("&7The &bSumo &7Event will start in &b00:10&7! "));

        message.then("[Click Here]")
                .color(ChatColor.GREEN)
                .command("/sumo join")
                .tooltip(ChatColor.GREEN + "Click to join!")
                .then(" (" + sumo.getRemainingPlayers().size() + "/" + sumo.getMaxPlayers() + ")")
                .color(ChatColor.WHITE);

        for (Player player : sumo.getPlayers()) {
            message.send(player);
        }
    }

    private void startRound(Sumo sumo, int players) {
        sumo.setState(SumoState.ROUND_STARTING);
        sumo.setTotalPlayers(players);
        sumo.onRound();
        sumo.setEventTask(new SumoRoundStartTask(sumo));
    }
}