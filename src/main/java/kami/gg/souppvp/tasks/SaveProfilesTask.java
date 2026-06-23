package kami.gg.souppvp.tasks;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;

public class SaveProfilesTask {

    public SaveProfilesTask(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SoupPvP.getInstance(), () -> {
            Long started = System.currentTimeMillis();
            for (Profile profile : SoupPvP.getInstance().getProfilesHandler().getProfiles().values()) {
                profile.saveProfile();
            }
            Long ended = System.currentTimeMillis();
            Bukkit.getLogger().info((CC.t("&a&lSuccessfully &asaved a total of &a&l" + SoupPvP.getInstance().getProfilesHandler().getProfiles().size() + "&a profiles within &a&l" + (ended - started) + "&ams.")));
        }, 0L, (120 * 20));
    }

}
