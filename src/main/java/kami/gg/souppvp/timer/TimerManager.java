package kami.gg.souppvp.timer;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.TaskUtil;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class TimerManager {

    private final Map<String, Timer> playerTimers;

    public TimerManager(SoupPvP plugin) {
        this.playerTimers = new LinkedHashMap<>();

        TaskUtil.runAsync(this::tickAll);
    }

    public void registerTimer(Timer timer) {
        playerTimers.put(timer.getName(), timer);
    }

    public Timer getTimer(String name) {
        return playerTimers.get(name);
    }

    public boolean hasTimer(String name) {
        return playerTimers.containsKey(name);
    }

    private void tickAll() {
        for (Timer timer : playerTimers.values()) {
            timer.tick();
        }
    }

    public void clear() {
        for (Timer timer : playerTimers.values()) {
            timer.clear();
        }
        playerTimers.clear();
    }
}
