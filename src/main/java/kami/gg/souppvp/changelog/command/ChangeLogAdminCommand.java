package kami.gg.souppvp.changelog.command;

import kami.gg.souppvp.changelog.menu.ChangeLogEditorMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ChangeLogAdminCommand extends Command {
    public ChangeLogAdminCommand(CommandManager manager) {
        super(manager, "changelogadmin");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("changelogedit", "cledit");
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

        if (!player.hasPermission("souppvp.changelog.admin")) {
            sendMessage(player, "&cYou don't have permission to use this command.");
            return;
        }

        new ChangeLogEditorMenu(player).open();
    }
}
