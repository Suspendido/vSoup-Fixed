package kami.gg.souppvp.events.impl.sumo.task;

import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.util.task.EventStartTask;
import kami.gg.souppvp.util.Cooldown;

/*
 * Copyright (c) 2026. @Comunidad, made since 26/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
*/
public class SumoStartTask extends EventStartTask {

    public SumoStartTask(Event event) {
        super(event, EventType.SUMO);
    }

    @Override
    protected Cooldown getCooldown() {
        return ((Sumo) event).getCooldown();
    }

    @Override
    protected void setCooldown(Cooldown cooldown) {
        ((Sumo) event).setCooldown(cooldown);
    }

    @Override
    protected void announce() {
        ((Sumo) event).announce();
    }

    @Override
    protected void onRound() {
        ((Sumo) event).onRound();
    }
}
