package kami.gg.souppvp.util;

import kami.gg.souppvp.SoupPvP;
import org.bukkit.scheduler.BukkitRunnable;

public class TasksUtility {

    public static void runTaskAsync(Runnable runnable) {
        SoupPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(SoupPvP.getInstance(), runnable);
    }

    public static void runTaskLater(Runnable runnable, long delay) {
        SoupPvP.getInstance().getServer().getScheduler().runTaskLater(SoupPvP.getInstance(), runnable, delay);
    }

    public static void runTaskTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(SoupPvP.getInstance(), delay, timer);
    }

    public static void runTaskTimer(Runnable runnable, long delay, long timer) {
        SoupPvP.getInstance().getServer().getScheduler().runTaskTimer(SoupPvP.getInstance(), runnable, delay, timer);
    }

    public static void runTask(Runnable runnable) {
        SoupPvP.getInstance().getServer().getScheduler().runTask(SoupPvP.getInstance(), runnable);
    }

    public static void runTaskLaterAsync(long delay, long period, Runnable runnable) {
        SoupPvP.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(SoupPvP.getInstance(), runnable, delay, period);
    }
}
