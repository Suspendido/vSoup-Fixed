package kami.gg.souppvp.events.impl.tnttag.task;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.impl.tnttag.TNTTagState;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class TNTTagTask extends BukkitRunnable {

    protected int ticks;
    protected final TNTTagGame game;
    protected final TNTTagState requiredState;

    protected TNTTagTask(TNTTagGame game, TNTTagState requiredState) {
        this.game = game;
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
        TNTTagGame active = SoupPvP.getInstance().getTntTagHandler().getActiveGame();
        return active == game && game.getState() == requiredState;
    }

    protected abstract void onRun();
    protected void onCancel() {}

    protected int getRemainingSeconds(int totalSeconds) {
        return Math.max(0, totalSeconds - ticks);
    }
}
