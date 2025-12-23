package kami.gg.souppvp.feats.staff.command;

import kami.gg.souppvp.feats.staff.StaffManager;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class VanishCommand extends Command {

    public VanishCommand(CommandManager manager) {
        super(
                manager,
                "vanish"
        );
        this.setPermissible("azurite.vanish");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList(
                "v",
                "vanished"
        );
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cThis command is for players only!");
            return;
        }

        StaffManager manager = getInstance().getStaffManager();

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sendMessage(sender, "&cPlayer " + args[0] + " does not exist!");
                return;
            }

            if (manager.isVanished(target)) {
                manager.disableVanish(target);
                sendMessage(target, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.DISABLED_VANISH"));
                sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.DISABLED_VANISH_TARGET")
                        .replace("%player%", target.getName())
                );
                return;
            }

            manager.enableVanish(target);
            sendMessage(target, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.ENABLED_VANISH"));
            sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.ENABLED_VANISH_TARGET")
                    .replace("%player%", target.getName())
            );
            return;
        }

        if (manager.isVanished(player)) {
            manager.disableVanish(player);
            sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.DISABLED_VANISH"));
            return;
        }

        manager.enableVanish(player);
        sendMessage(sender, getInstance().getStaffManager().getStaffConfig().getString("STAFF_MODE.ENABLED_VANISH"));
    }
}