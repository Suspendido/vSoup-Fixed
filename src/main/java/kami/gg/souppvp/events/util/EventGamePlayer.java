package kami.gg.souppvp.events.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class EventGamePlayer {

    private final UUID uuid;
    private final String username;
    private int roundWins;
    private EventPlayerState state;

    public EventGamePlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
        this.roundWins = 0;
        this.state = EventPlayerState.WAITING;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void incrementRoundWins() {
        this.roundWins++;
    }

}
