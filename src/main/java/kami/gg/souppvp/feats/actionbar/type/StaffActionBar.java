package kami.gg.souppvp.feats.actionbar.type;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.actionbar.ActionBarPriority;
import kami.gg.souppvp.feats.actionbar.ActionBarProvider;
import kami.gg.souppvp.util.CC;
import org.bukkit.entity.Player;

/*
 * Copyright (c) 2026. @Comunidad, made since 28/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class StaffActionBar implements ActionBarProvider {
    @Override
    public String getActionBar(Player player) {
        String message = SoupPvP.getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.ACTION_BAR_STRING")
                .replace("%vanished%", SoupPvP.getInstance().getStaffManager().isVanished(player) ? "&a✔" : "&c✖")
                .replace("%hidestaff%", SoupPvP.getInstance().getStaffManager().isHideStaff(player) ? "&a✔" : "&c✖");

        return CC.t(message);
    }

    @Override
    public ActionBarPriority priority() {
        return ActionBarPriority.STAFF;
    }

    @Override
    public boolean isActive(Player player) {
        return SoupPvP.getInstance().getStaffManager().isStaffEnabled(player);
    }
}
