package kami.gg.souppvp.events.impl.sumo.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SumoTpArg extends Argument {

    public SumoTpArg(CommandManager manager) {
        super(manager, Arrays.asList("tp", "teleport"));
        this.setPermissible("souppvp.sumotp");
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CC.translate("&cOnly players can run this command."));
            return;
        }

        player.teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
        player.sendMessage(CC.translate("&aSuccessfully teleported to the sumo system's spectator spawn location."));
    }
}
