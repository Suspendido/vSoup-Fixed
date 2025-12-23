package kami.gg.souppvp.feats.staff.command;

import kami.gg.souppvp.feats.staff.menu.StaffOnlineMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class StaffOnlineCommand extends Command {

    public StaffOnlineCommand(CommandManager manager) {
        super(
                manager,
                "staffonline"
        );
        this.setPermissible("azurite.staffonline");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
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

        new StaffOnlineMenu(player).openMenu(player);
    }
}