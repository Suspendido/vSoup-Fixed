package kami.gg.souppvp.feats.hooks.staff.type;

import kami.gg.souppvp.feats.hooks.staff.Staff;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.entity.Player;

public class AquaCoreStaff implements Staff {
    @Override
    public boolean isStaffEnabled(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerData(player.getUniqueId()).isInStaffMode();
    }

    @Override
    public boolean isVanishEnabled(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerData(player.getUniqueId()).isVanished();
    }
}
