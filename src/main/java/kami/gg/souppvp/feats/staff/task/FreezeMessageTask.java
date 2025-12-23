package kami.gg.souppvp.feats.staff.task;

import kami.gg.souppvp.feats.staff.StaffManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FreezeMessageTask extends BukkitRunnable {

    private final StaffManager manager;
    private final Player player;

    public FreezeMessageTask(StaffManager manager, Player player) {
        this.manager = manager;
        this.player = player;
        this.runTaskTimer(manager.getInstance(), 0L, 20L * manager.getStaffConfig().getInt("STAFF_MODE.FREEZE_MESSAGE_INTERVAL"));
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        }

        for (String s : manager.getStaffConfig().getStringList("STAFF_MODE.VANISH_INTERVAL_MESSAGE")) {
            if (s.isEmpty()) return;
            player.sendMessage(s);
        }
    }
}