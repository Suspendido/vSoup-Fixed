package kami.gg.souppvp.command;

import kami.gg.souppvp.options.OptionsMenu;
import kami.gg.souppvp.util.CC;
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
            sender.sendMessage(CC.translate("&cOnly players can run this command."));
            return;
        }

        new OptionsMenu().openMenu(player);
    }
}
