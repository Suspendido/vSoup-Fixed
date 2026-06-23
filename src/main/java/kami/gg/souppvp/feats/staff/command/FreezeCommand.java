package kami.gg.souppvp.feats.staff.command;

import kami.gg.souppvp.feats.staff.StaffManager;
import kami.gg.souppvp.feats.staff.task.FreezeMessageTask;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class FreezeCommand extends Command {

    private final Map<UUID, FreezeMessageTask> tasks;

    public FreezeCommand(CommandManager manager) {
        super(
                manager,
                "freeze"
        );
        this.tasks = new HashMap<>();
        this.setPermissible("azurite.freeze");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList(
                "ss"
        );
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList(CC.t("&cUsage: /freeze <player>"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        StaffManager staffManager = getInstance().getStaffManager();
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sendMessage(sender, "&cPlayer " + args[0] + " does not exist!");
            return;
        }

        if (target.hasPermission("azurite.freeze.bypass")) {
            sendMessage(sender, "&cYou dont have the power to freeze this player!");
            return;
        }

        if (staffManager.isFrozen(target)) {
            staffManager.unfreezePlayer(target);
            tasks.get(target.getUniqueId()).cancel();
            sendMessage(sender, "&aYou have unfrozen " + target.getDisplayName() + "&a!");
            return;
        }

        staffManager.freezePlayer(target);
        tasks.put(target.getUniqueId(), new FreezeMessageTask(getInstance().getStaffManager(), target));
        sendMessage(sender, "&aYou have frozen " + target.getDisplayName() + "&a!");
    }
}