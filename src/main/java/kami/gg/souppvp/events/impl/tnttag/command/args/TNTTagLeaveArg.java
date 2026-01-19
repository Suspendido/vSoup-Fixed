package kami.gg.souppvp.events.impl.tnttag.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TNTTagLeaveArg extends Argument {

    public TNTTagLeaveArg(CommandManager manager) {
        super(manager, Collections.singletonList("leave"));
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

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        TNTTagGame activeSumo = SoupPvP.getInstance().getTntTagHandler().getActiveGame();

        if (activeSumo == null) {
            sendMessage(player, "&cThere isn't an active tnttag event.");
            return;
        }

        if (profile.getTntTagGame() == null || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
            sendMessage(player, "&cYou are not apart of the active tnttag event.");
            return;
        }

        SoupPvP.getInstance().getTntTagHandler().getActiveGame().handleLeave(player);
    }
}
