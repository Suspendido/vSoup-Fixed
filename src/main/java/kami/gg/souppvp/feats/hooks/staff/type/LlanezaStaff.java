package kami.gg.souppvp.feats.hooks.staff.type;

import com.github.llanezsa.staff.staff.StaffHandler;
import kami.gg.souppvp.feats.hooks.staff.Staff;
import org.bukkit.entity.Player;

public class LlanezaStaff implements Staff {

    @Override
    public boolean isStaffEnabled(Player player) {
        StaffHandler staff = com.github.llanezsa.staff.Staff.get().getStaffHandler();
        return staff.onStaffMode(player);
    }

    @Override
    public boolean isVanishEnabled(Player player) {
        StaffHandler vanish = com.github.llanezsa.staff.Staff.get().getStaffHandler();
        return vanish.isVanished(player);
    }
}
