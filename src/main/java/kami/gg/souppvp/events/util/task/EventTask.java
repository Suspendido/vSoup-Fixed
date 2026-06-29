package kami.gg.souppvp.events.util.task;

import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.EventManager;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.SoupPvP;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class EventTask extends BukkitRunnable {

    protected int ticks;
    protected final Event event;
    protected final EventType eventType;
    protected final EventState requiredState;

    protected EventTask(Event event, EventType eventType, EventState requiredState) {
        this.event = event;
        this.eventType = eventType;
        this.requiredState = requiredState;
    }

    @Override
    public final void run() {
        if (!isValid()) {
            onCancel();
            cancel();
            return;
        }

        ticks++;
        onRun();
    }

    private boolean isValid() {
        Event active = SoupPvP.getInstance().getEventManager().getActiveEvent(eventType);
        return active == event && event.getState() == requiredState;
    }

    protected abstract void onRun();
    protected void onCancel() {}

    protected int getRemainingSeconds(int totalSeconds) {
        return Math.max(0, totalSeconds - ticks);
    }
}
