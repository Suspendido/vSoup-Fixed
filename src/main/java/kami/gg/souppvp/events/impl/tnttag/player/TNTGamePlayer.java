package kami.gg.souppvp.events.impl.tnttag.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class TNTGamePlayer {

    private final UUID uuid;
    private final String username;
    private TNTGamePlayerState state = TNTGamePlayerState.WAITING;
    private int roundWins = 0;

    public TNTGamePlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void incrementRoundWins() {
        this.roundWins++;
    }
}
