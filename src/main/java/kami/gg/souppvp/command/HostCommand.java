package kami.gg.souppvp.command;

import kami.gg.souppvp.events.menu.HostEventsMenu;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class HostCommand extends kami.gg.souppvp.util.command.Command {

    public HostCommand(CommandManager manager) {
        super(manager, "host");
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
        Player player = (Player) sender;
        new HostEventsMenu().openMenu(player);
    }
}
