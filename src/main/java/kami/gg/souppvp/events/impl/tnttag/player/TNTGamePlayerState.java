package kami.gg.souppvp.events.impl.tnttag.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TNTGamePlayerState {

    WAITING("Waiting"),
    ELIMINATED("Eliminated");

    private final String readable;
}
