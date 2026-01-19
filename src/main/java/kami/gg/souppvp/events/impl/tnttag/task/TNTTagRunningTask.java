package kami.gg.souppvp.events.impl.tnttag.task;

import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.impl.tnttag.TNTTagState;

public class TNTTagRunningTask extends TNTTagTask {

    public TNTTagRunningTask(TNTTagGame game) {
        super(game, TNTTagState.RUNNING);
    }

    @Override
    protected void onRun() {
        TNTTagGame game = getGame();
        int remaining = game.getTimeRemaining();

        if (remaining <= 0) {
            game.explode();
        }
    }
}