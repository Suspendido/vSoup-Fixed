package kami.gg.souppvp.events;

import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.events.util.task.EventStartTask;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

public interface Event {

    EventState getState();
    EventType getType();
    Player getWinner();
    String getEventName();
    List<Player> getRemainingPlayers();

    int getMaxPlayers();
    int getTotalPlayers();

    void setTotalPlayers(int totalPlayers);
    boolean canEnd();

    boolean hasPlayer(Player player);
    void end();

    void handleJoin(Player player);
    void handleLeave(Player player);

    boolean hasRounds();
}