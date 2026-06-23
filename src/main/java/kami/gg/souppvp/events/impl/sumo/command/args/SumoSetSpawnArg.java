package kami.gg.souppvp.events.impl.sumo.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SumoSetSpawnArg extends Argument {

    public SumoSetSpawnArg(CommandManager manager) {
        super(manager, Collections.singletonList("setspawn"));
        this.setPermissible("souppvp.sumosetspawn");
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CC.t("&cOnly players can run this command."));
            return;
        }

        String position = args[0];

        switch (position) {
            case "a":
                SoupPvP.getInstance().getSumoHandler().setSpawnA(player.getLocation());
                player.sendMessage(CC.t("&aSuccessfully updated the sumo system's spawn a location."));
                SoupPvP.getInstance().getSumoHandler().save();
                break;
            case "b":
                SoupPvP.getInstance().getSumoHandler().setSpawnB(player.getLocation());
                player.sendMessage(CC.t("&aSuccessfully updated the sumo system's spawn b location."));
                SoupPvP.getInstance().getSumoHandler().save();
                break;
            case "spec":
                SoupPvP.getInstance().getSumoHandler().setSpectatorSpawn(player.getLocation());
                player.sendMessage(CC.t("&aSuccessfully updated the sumo system's spectator spawn location."));
                SoupPvP.getInstance().getSumoHandler().save();
                break;
            default:
                player.sendMessage(CC.t("&cAvailable Positions: a,b,spec"));
                break;
        }
    }
}
