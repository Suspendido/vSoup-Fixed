package kami.gg.souppvp.events.impl.tnttag.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TNTTagTPArg extends Argument {

    public TNTTagTPArg(CommandManager manager) {
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
            sendMessage(sender, "&cOnly players can run this command.");
            return;
        }

        player.teleport(SoupPvP.getInstance().getTntTagHandler().getSpectatorSpawn());
        sendMessage(player, "&aSuccessfully teleported to the tnttag system's spectator spawn location.");
    }
}
