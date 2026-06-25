package kami.gg.souppvp.command;

import kami.gg.souppvp.perk.menu.AllPerksMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PerksCommand extends Command {

    public PerksCommand(CommandManager manager) {
        super(manager, "perks");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("perk");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cOnly players can run this command.");
            return;
        }

        new AllPerksMenu(player).open();
    }
}
