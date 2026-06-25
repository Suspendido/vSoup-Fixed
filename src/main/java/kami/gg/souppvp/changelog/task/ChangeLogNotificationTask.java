package kami.gg.souppvp.changelog.task;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChangeLogNotificationTask extends BukkitRunnable {

    private final SoupPvP plugin;
    private static final long NOTIFICATION_INTERVAL = 300L; // 15 seconds (20 ticks * 15)

    public ChangeLogNotificationTask(SoupPvP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getChangeLogHandler() != null && plugin.getChangeLogHandler().hasUnreadChangeLogs(player.getUniqueId())) {
                player.sendMessage(CC.t("&e&lNEW CHANGELOG! &7There is a new changelog available. Type &e/changelog&7 to view it."));
            }
        }
    }

    public static void start(SoupPvP plugin) {
        new ChangeLogNotificationTask(plugin).runTaskTimer(plugin, NOTIFICATION_INTERVAL, NOTIFICATION_INTERVAL);
    }
}
