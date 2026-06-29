package kami.gg.souppvp.events.util.task;

import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.util.EventState;

public abstract class EventEndTask extends EventTask {

    public EventEndTask(Event event, EventType eventType) {
        super(event, eventType, EventState.ENDING);
    }

    @Override
    protected void onRun() {
        if (event.canEnd()) {
            event.end();
            return;
        }

        // Si el evento tiene rondas, iniciar nueva ronda después de 3 ticks
        if (event.hasRounds() && getTicks() >= 3) {
            onRound();
        }
    }

    protected void onRound() {}
}
