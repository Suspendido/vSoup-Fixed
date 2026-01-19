package kami.gg.souppvp.events.impl.sumo.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SumoSpecArg extends Argument {

    public SumoSpecArg(CommandManager manager) {
        super(manager, Collections.singletonList("spec"));
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

        Sumo sumo = SoupPvP.getInstance().getSumoHandler().getActiveSumo();
        if (sumo == null) {
            sendMessage(player, "&cThere is no sumo game available.");
            return;
        }
        sumo.addSpectator(player);
        sendMessage(player, "&aSuccessfully teleported to the sumo system's spectator spawn location.");
    }
}
