package kami.gg.souppvp.events.impl.tnttag.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TNTTagSetSpawnArg extends Argument {

    public TNTTagSetSpawnArg(CommandManager manager) {
        super(manager, Collections.singletonList("setspawn"));
        this.setPermissible("souppvp.tnttagsetspawn");
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

        String type = args[0];

        switch (type) {
            case "event":
                SoupPvP.getInstance().getTntTagHandler().setEventSpawn(player.getLocation());
                player.sendMessage(CC.translate("&aSuccessfully updated the tnttag system's event spawn a location."));
                SoupPvP.getInstance().getTntTagHandler().save();
                break;
            case "spectator":
            case "spec":
                SoupPvP.getInstance().getTntTagHandler().setSpectatorSpawn(player.getLocation());
                player.sendMessage(CC.translate("&aSuccessfully updated the tnttag system's spectator spawn location."));
                SoupPvP.getInstance().getTntTagHandler().save();
                break;
            default:
                sendMessage(player, "&cAvailable Options: event, spec");
        }
    }
}
