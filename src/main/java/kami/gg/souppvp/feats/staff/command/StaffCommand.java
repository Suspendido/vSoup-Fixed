package kami.gg.souppvp.feats.staff.command;

import kami.gg.souppvp.feats.staff.StaffManager;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class StaffCommand extends Command {

    public StaffCommand(CommandManager manager) {
        super(
                manager,
                "staff"
        );
        this.setPermissible("azurite.staff");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "mod",
                "modmode",
                "sm",
                "staffmode",
                "h",
                "mm"
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

        StaffManager manager = getInstance().getStaffManager();

        if (args.length == 1 && player.hasPermission("azurite.staff.other")) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sendMessage(sender, "&cPlayer " + args[0] + " does not exist!");
                return;
            }

            if (manager.isStaffEnabled(target)) {
                manager.disableStaff(target);
                sendMessage(target, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.DISABLED_STAFF"));
                sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.DISABLED_STAFF_TARGET")
                        .replace("%player%", target.getName())
                );
                return;
            }

            manager.enableStaff(target);
            sendMessage(target, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.ENABLED_STAFF"));
            sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.ENABLED_STAFF_TARGET")
                    .replace("%player%", target.getName())
            );
            return;
        }

        if (manager.isStaffEnabled(player)) {
            manager.disableStaff(player);
            sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.DISABLED_STAFF"));
            return;
        }

        manager.enableStaff(player);
        sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.ENABLED_STAFF"));
    }
}