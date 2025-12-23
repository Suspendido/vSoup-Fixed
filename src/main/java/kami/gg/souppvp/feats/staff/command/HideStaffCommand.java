package kami.gg.souppvp.feats.staff.command;

import kami.gg.souppvp.feats.staff.Staff;
import kami.gg.souppvp.feats.staff.StaffManager;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class HideStaffCommand extends Command {

    public HideStaffCommand(CommandManager manager) {
        super(
                manager,
                "hidestaff"
        );
        this.setPermissible("azurite.hidestaff");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList(
                "hs"
        );
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cThis is for players only!");
            return;
        }

        StaffManager staffManager = getInstance().getStaffManager();

        if (staffManager.isHideStaff(player)) {
            // Remove
            staffManager.getHideStaff().remove(player.getUniqueId());

            // Show if they are only vanished
            for (Staff staff : staffManager.getStaffMembers().values()) {
                if (staffManager.isVanished(staff.getPlayer())) {
                    player.showPlayer(staff.getPlayer());
                }
            }

            sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("HIDE_STAFF_COMMAND.DISABLED"));
            return;
        }

        staffManager.getHideStaff().add(player.getUniqueId());
        sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("HIDE_STAFF_COMMAND.ENABLED"));

        // Hide if they are only vanished
        for (Staff staff : staffManager.getStaffMembers().values()) {
            if (staffManager.isVanished(staff.getPlayer())) {
                player.hidePlayer(staff.getPlayer());
            }
        }
    }
}