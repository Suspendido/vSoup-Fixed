package kami.gg.souppvp.tasks;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CanaPerkAndFiremanKitTask {

    private final Perk canaPerk;
    private final Kit firemanKit;

    public CanaPerkAndFiremanKitTask() {

        this.canaPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Cana");
        this.firemanKit = SoupPvP.getInstance().getKitsHandler().getKitByName("Fireman");

        Bukkit.getScheduler().scheduleSyncRepeatingTask(SoupPvP.getInstance(), () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {

                Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
                if (profile == null) continue;

                Material type = player.getLocation().getBlock().getType();
                if (type != Material.WATER && type != Material.STATIONARY_WATER) continue;

                boolean hasCana = false;

                if (!profile.getActivePerks().isEmpty()) {
                    for (String perkName : profile.getActivePerks()) {
                        Perk perkObj = SoupPvP.getInstance().getPerksHandler().getPerkByName(perkName);
                        if (perkObj == canaPerk) {
                            hasCana = true;
                            break;
                        }
                    }
                }

                boolean isFireman = firemanKit != null && firemanKit.getName().equalsIgnoreCase(profile.getCurrentKit());

                if (hasCana || isFireman) {
                    player.damage(2);
                }
            }

        }, 0L, 5L);
    }
}
