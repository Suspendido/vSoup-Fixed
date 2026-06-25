package kami.gg.souppvp.command;

import kami.gg.souppvp.killstreak.menu.KillstreakMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class KillstreakCommand extends Command {

    public KillstreakCommand(CommandManager manager) {
        super(
                manager,
                "killstreak"
        );
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("ks", "killstreaks");
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

        new KillstreakMenu(player).open();
    }
}
