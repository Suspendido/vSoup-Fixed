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

    public CanaPerkAndFiremanKitTask() {

        this.canaPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Cana");

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

                Kit currentKit = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit());
                boolean hasFiremanAbility = currentKit != null && 
                    ((currentKit.getPrimaryAbility() != null && currentKit.getPrimaryAbility().getName().equals("Fireman")) ||
                     (currentKit.getSecondaryAbility() != null && currentKit.getSecondaryAbility().getName().equals("Fireman")));

                if (hasCana || hasFiremanAbility) {
                    player.damage(2);
                }
            }

        }, 0L, 5L);
    }
}
