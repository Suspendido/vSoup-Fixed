package kami.gg.souppvp.command;

import kami.gg.souppvp.profile.menus.options.OptionsMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class OptionsCommand extends Command {

    public OptionsCommand(CommandManager manager) {
        super(manager, "options");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("option", "setting", "settings");
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

        new OptionsMenu(player).open();
    }
}
