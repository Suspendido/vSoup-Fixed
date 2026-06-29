package kami.gg.souppvp.events.impl.tnttag.task;

import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.util.task.EventStartTask;
import kami.gg.souppvp.util.Cooldown;

public class TNTTagStartTask extends EventStartTask {

    public TNTTagStartTask(TNTTagGame game) {
        super(game, EventType.TNTTAG);
    }

    @Override
    protected Cooldown getCooldown() {
        return ((TNTTagGame) event).getCooldown();
    }

    @Override
    protected void setCooldown(Cooldown cooldown) {
        ((TNTTagGame) event).setCooldown(cooldown);
    }

    @Override
    protected void announce() {
        ((TNTTagGame) event).announce();
    }

    @Override
    protected void onRound() {
        ((TNTTagGame) event).onRound();
    }
}
