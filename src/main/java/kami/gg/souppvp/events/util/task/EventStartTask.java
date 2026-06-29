package kami.gg.souppvp.events.util.task;

import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.Cooldown;
import kami.gg.souppvp.util.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class EventStartTask extends EventTask {

    private static final int FORCE_END_TICKS = 120;
    private static final int MIN_PLAYERS = 2;
    private static final int START_AFTER_TICKS = 30;
    private static final int ANNOUNCE_INTERVAL = 10;

    public EventStartTask(Event event, EventType eventType) {
        super(event, eventType, EventState.WAITING);
    }

    @Override
    protected void onRun() {
        int players = event.getRemainingPlayers().size();

        // Force end
        if (getTicks() >= FORCE_END_TICKS) {
            if (players <= 1) {
                Bukkit.broadcastMessage("&cThe " + eventType.getColor() + eventType.getName() + " Event has been cancelled due to lack of players.");
                event.end();
                return;
            }
            event.end();
            return;
        }

        // Not enough players anymore
        if (players <= 1 && getCooldown() != null) {
            setCooldown(null);
            Bukkit.broadcastMessage("&cThere are not enough players for the " + eventType.getColor() + eventType.getName() + " Event to start.");
            return;
        }

        // Check if should start
        if (shouldStart(players)) {
            handleStartCountdown(players);
        }

        // Periodic announce
        if (getTicks() % ANNOUNCE_INTERVAL == 0) {
            announce();
        }
    }

    private boolean shouldStart(int players) {
        return players == event.getMaxPlayers() || (getTicks() >= START_AFTER_TICKS && players >= MIN_PLAYERS);
    }

    private void handleStartCountdown(int players) {
        if (getCooldown() == null) {
            startCooldown(players);
            return;
        }

        if (getCooldown().hasExpired()) {
            startEvent(players);
        }
    }

    private void startCooldown(int players) {
        setCooldown(new Cooldown(11_000));

        FancyMessage message = new FancyMessage(
                CC.t("&7The " + eventType.getColor() + eventType.getName() + " &7Event will start in &b00:10&7! ")
        );

        message.then("[Click Here]")
                .color(ChatColor.GREEN)
                .command("/event " + eventType.name().toLowerCase() + " join")
                .tooltip(ChatColor.GREEN + "Click to join!")
                .then(" (" + players + "/" + event.getMaxPlayers() + ")")
                .color(ChatColor.WHITE);

        for (Player player : Bukkit.getOnlinePlayers()) {
            message.send(player);
        }
    }

    private void startEvent(int players) {
        event.setTotalPlayers(players);
        Bukkit.broadcastMessage("&aThe " + eventType.getColor() + eventType.getName() + " Event is starting!");
        onRound();
    }

    // Métodos abstractos que cada evento debe implementar
    protected abstract Cooldown getCooldown();
    protected abstract void setCooldown(Cooldown cooldown);
    protected abstract void announce();
    protected abstract void onRound();
}
