package kami.gg.souppvp.tablist.task;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.tablist.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistTask implements Runnable {

    @Override
    public void run() {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                TablistManager manager = SoupPvP.getInstance().getTablistManager();

                if (manager != null) {
                    manager.updatePlayerTablist(player);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
