package kami.gg.souppvp.events.impl.sumo.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SumoPlayerState {

	WAITING("Waiting"),
	ELIMINATED("Eliminated");

	private final String readable;

}
