package kami.gg.souppvp.command.bounty;

import kami.gg.souppvp.bounty.BountyMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BountyListCommand extends Command {

    public BountyListCommand(CommandManager manager) {
        super(
                manager,
                "listbounty"
        );
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
        new BountyMenu(player).open();
    }
}
