package kami.gg.souppvp.feats.hooks.staff;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.hooks.staff.type.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class StaffHook implements Staff {
    private Staff staff;

    public StaffHook() {
        super();
        this.load();
    }

    private void load() {
        if (verifyPlugin("Staff", SoupPvP.getInstance())) {
            staff = new LlanezaStaff();
        }

        if (verifyPlugin("AquaCore", SoupPvP.getInstance())) {
            staff = new AquaCoreStaff();
        }
    }

    public static boolean verifyPlugin(String plugin, SoupPvP instance) {
        PluginManager pm = instance.getServer().getPluginManager();
        return pm.getPlugin(plugin) != null;
    }

    @Override public boolean isStaffEnabled(Player player) {
        return staff.isStaffEnabled(player);
    }
    @Override public boolean isVanishEnabled(Player player) {
        return staff.isVanishEnabled(player);
    }
}
