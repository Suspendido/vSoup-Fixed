package kami.gg.souppvp.changelog.command;

import kami.gg.souppvp.changelog.menu.ChangeLogViewMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ChangeLogCommand extends Command {
    public ChangeLogCommand(CommandManager manager) {
        super(manager, "changelog");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("changelogs", "news");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cOnly players can use this command.");
            return;
        }

        new ChangeLogViewMenu(player).open();
    }
}
