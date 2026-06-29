package kami.gg.souppvp.command;

import kami.gg.souppvp.perk.menu.PerkToggleMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TogglePerksCommand extends Command {

    public TogglePerksCommand(CommandManager manager) {
        super(manager, "toggleperks");
        this.permissible = "souppvp.admin";
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("tperks");
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

        new PerkToggleMenu(player).open();
    }
}
