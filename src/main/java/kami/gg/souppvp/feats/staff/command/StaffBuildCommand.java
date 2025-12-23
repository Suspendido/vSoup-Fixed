package kami.gg.souppvp.feats.staff.command;

import kami.gg.souppvp.feats.staff.StaffManager;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StaffBuildCommand extends Command {

    public StaffBuildCommand(CommandManager manager) {
        super(
                manager,
                "staffbuild"
        );
        this.setPermissible("azurite.staffbuild");
    }

    @Override
    public List<String> aliases() {
        return List.of("build", "sbuild");
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

        if (!staffManager.isStaffEnabled(player)) {
            sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_BUILD_COMMAND.NOT_IN_STAFF"));
            return;
        }

        if (staffManager.isStaffBuild(player)) {
            staffManager.getStaffBuild().remove(player.getUniqueId());
            sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_BUILD_COMMAND.BUILD_DISABLED"));
            return;
        }

        staffManager.getStaffBuild().add(player.getUniqueId());
        sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_BUILD_COMMAND.BUILD_ENABLED"));
    }
}