package kami.gg.souppvp.command;

import kami.gg.souppvp.tier.menu.TiersProgressMenu;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TiersCommand extends Command {

    public TiersCommand(CommandManager manager) {
        super(manager, "tiers");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("tier");
    }

    @Override
    public List<String> usage() {
        return List.of();
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CC.translate("&cOnly players can run this command."));
            return;
        }

        new TiersProgressMenu().openMenu(player);
    }
}
