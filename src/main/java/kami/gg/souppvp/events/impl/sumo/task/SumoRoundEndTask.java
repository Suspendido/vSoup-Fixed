package kami.gg.souppvp.events.impl.sumo.task;

import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.impl.sumo.SumoState;

public class SumoRoundEndTask extends SumoTask {

	public SumoRoundEndTask(Sumo sumo) {
		super(sumo, SumoState.ROUND_ENDING);
	}

    @Override
    public void onRun() {
        Sumo sumo = getSumo();

        if (sumo.canEnd()) {
            sumo.end();
            return;
        }

        if (getTicks() >= 3) {
            sumo.onRound();
        }
    }

}
