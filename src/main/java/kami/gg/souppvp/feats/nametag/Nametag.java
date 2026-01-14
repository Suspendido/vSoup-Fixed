package kami.gg.souppvp.feats.nametag;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class Nametag {

    private final Player player;

    public Nametag(Player player) {
        this.player = player;
    }
}