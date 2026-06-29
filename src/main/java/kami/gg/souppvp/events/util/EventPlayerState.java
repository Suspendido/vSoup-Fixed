package kami.gg.souppvp.events.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventPlayerState {

    WAITING("Waiting"),
    WINNER("Winner"),
    ELIMINATED("Eliminated");

    private final String readable;
}
