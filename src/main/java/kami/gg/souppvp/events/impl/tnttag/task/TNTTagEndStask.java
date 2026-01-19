package kami.gg.souppvp.events.impl.tnttag.task;

import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.impl.tnttag.TNTTagState;

public class TNTTagEndStask extends TNTTagTask {

    public TNTTagEndStask(TNTTagGame game) {
        super(game, TNTTagState.ENDING);
    }

    @Override
    public void onRun() {
        TNTTagGame game = getGame();

        if (game.canEnd()) {
            game.end();
        }
    }
}
