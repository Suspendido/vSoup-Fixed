package kami.gg.souppvp.kit.progress;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.entity.Player;

public class KitProgressManager {

    private final SoupPvP plugin;

    public KitProgressManager(SoupPvP plugin) {
        this.plugin = plugin;
    }

    public KitProgress getProgress(Profile profile) {
        return profile.getKitProgress(profile.getCurrentKit());
    }

    public void handleKitUse(Profile profile) {
        KitProgress progress = getProgress(profile);
        progress.setTimesUsed(progress.getTimesUsed() + 1);
    }

    public void handleDeath(Profile profile) {
        KitProgress progress = getProgress(profile);
        progress.setDeaths(progress.getDeaths() + 1);
    }

    public void handleKill(Profile killerProfile) {
        KitProgress progress = getProgress(killerProfile);
        progress.setKills(progress.getKills() + 1);
        addExp(killerProfile, progress, 10);
    }

    private void addExp(Profile profile, KitProgress progress, int amount) {
        progress.setExp(progress.getExp() + amount);

        int required = progress.getLevel() * 100;

        if (progress.getExp() >= required) {
            progress.setExp(0);
            progress.setLevel(progress.getLevel() + 1);

            Player player = plugin.getServer().getPlayer(profile.getUuid());
            if (player != null) {
                player.sendMessage(CC.translate("&6Your " + profile.getCurrentKit() + " kit leveled up to &e" + progress.getLevel()));
            }
        }
    }
}
