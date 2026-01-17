package kami.gg.souppvp.events.impl.sumo.task;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.impl.sumo.SumoState;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class SumoTask extends BukkitRunnable {

    protected int ticks;
    protected final Sumo sumo;
    protected final SumoState requiredState;

    protected SumoTask(Sumo sumo, SumoState requiredState) {
        this.sumo = sumo;
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
        Sumo active = SoupPvP.getInstance().getSumoHandler().getActiveSumo();
        return active == sumo && sumo.getState() == requiredState;
    }


    protected abstract void onRun();
    protected void onCancel() {}

    protected int getRemainingSeconds(int totalSeconds) {
        return Math.max(0, totalSeconds - ticks);
    }
}
