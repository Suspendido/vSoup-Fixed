package kami.gg.souppvp.feats.freekits;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.lang.Lang;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.LocalDate;

/*
 * Copyright (c) 2026. @Comunidad, made since 1/7/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class FreeKitsTask extends BukkitRunnable {

    private boolean wasFriday = false;

    public FreeKitsTask() {
        this.runTaskTimerAsynchronously(SoupPvP.getInstance(), 0, 1200L);
    }

    @Override
    public void run() {
        LocalDate date = LocalDate.now();
        DayOfWeek day = date.getDayOfWeek();
        boolean isFriday = day == DayOfWeek.FRIDAY;

        if (isFriday && !wasFriday && !SoupPvP.getIsFreeKitsMode()) {
            // Activate free kits mode
            Bukkit.getScheduler().runTask(SoupPvP.getInstance(), () -> {
                SoupPvP.setIsFreeKitsMode(true);
                SoupPvP.getInstance().getConfig().set("FREE-KITS", true);
                SoupPvP.getInstance().saveConfig();
                SoupPvP.getInstance().reloadConfig();

                // Broadcast message
                if (Lang.FREE_KITS_ACTIVATED != null) {
                    for (String line : Lang.FREE_KITS_ACTIVATED) {
                        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(line));
                    }
                }
            });
        } else if (!isFriday && wasFriday && SoupPvP.getIsFreeKitsMode()) {
            // Deactivate free kits mode when Friday ends
            Bukkit.getScheduler().runTask(SoupPvP.getInstance(), () -> {
                SoupPvP.setIsFreeKitsMode(false);
                SoupPvP.getInstance().getConfig().set("FREE-KITS", false);
                SoupPvP.getInstance().saveConfig();
                SoupPvP.getInstance().reloadConfig();

                // Broadcast message
                if (Lang.FREE_KITS_DEACTIVATED != null) {
                    for (String line : Lang.FREE_KITS_DEACTIVATED) {
                        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(CC.t(line)));
                    }
                }
            });
        }

        wasFriday = isFriday;
    }
}
