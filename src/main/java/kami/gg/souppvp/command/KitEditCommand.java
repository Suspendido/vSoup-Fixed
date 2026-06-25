package kami.gg.souppvp.command;

import kami.gg.souppvp.kit.menu.editor.KitEditSelectMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class KitEditCommand extends Command {
    public KitEditCommand(CommandManager manager) {
        super(manager, "kitedit");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("editkit", "kiteditor");
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

        if (!player.hasPermission("souppvp.admin")) {
            sendMessage(player, "&cYou don't have permission to use this command.");
            return;
        }

        new KitEditSelectMenu(player).open();
    }
}
