package kami.gg.souppvp.util;

import kami.gg.souppvp.SoupPvP;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {

    public static void run(Runnable runnable) {
        SoupPvP.getInstance().getServer().getScheduler().runTask(SoupPvP.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        SoupPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(SoupPvP.getInstance(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        SoupPvP.getInstance().getServer().getScheduler().runTaskTimer(SoupPvP.getInstance(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(SoupPvP.getInstance(), delay, timer);
    }

    public static void runTimerAsync(Runnable runnable, long delay, long period) {
        SoupPvP.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(SoupPvP.getInstance(), runnable, delay, period);
    }

    public static void runLater(Runnable runnable, long delay) {
        SoupPvP.getInstance().getServer().getScheduler().runTaskLater(SoupPvP.getInstance(), runnable, delay);
    }

    public static void runLaterAsync(Runnable runnable, long delay) {
        SoupPvP.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(SoupPvP.getInstance(), runnable, delay);
    }

}
