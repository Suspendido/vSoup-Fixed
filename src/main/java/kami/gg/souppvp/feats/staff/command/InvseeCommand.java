package kami.gg.souppvp.feats.staff.command;

import kami.gg.souppvp.feats.staff.menu.InspectionMenu;
import kami.gg.souppvp.lang.Lang;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class InvseeCommand extends Command {

    public InvseeCommand(CommandManager manager) {
        super(
                manager,
                "invsee"
        );
        this.setPermissible("souppvp.invsee");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return getInstance().getStaffManager().getStaffConfig().getStringList("INVSEE_COMMAND.USAGE");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, Lang.ONLY_PLAYERS);
            return;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sendMessage(sender, Lang.PLAYER_NOT_FOUND
                    .replace("{player}", args[0])
            );
            return;
        }

        new InspectionMenu(target).open();
    }
}