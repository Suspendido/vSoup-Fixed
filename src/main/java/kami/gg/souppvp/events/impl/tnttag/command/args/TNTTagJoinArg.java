package kami.gg.souppvp.events.impl.tnttag.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.impl.tnttag.TNTTagState;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TNTTagJoinArg extends Argument {

    public TNTTagJoinArg(CommandManager manager) {
        super(manager, Collections.singletonList("join"));
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
        TNTTagGame activeTNTTag = SoupPvP.getInstance().getTntTagHandler().getActiveGame();

        if (!profile.getProfileState().equals(ProfileState.SPAWN)) {
            player.sendMessage(CC.t("&cYou cannot join the tnttag event right now. You need to be at spawn."));
            return;
        }

        if (activeTNTTag == null) {
            sendMessage(player, "&cThere isn't an active tnttag event.");
            return;
        }

        if (activeTNTTag.getState() != TNTTagState.WAITING) {
            sendMessage(player, "&cThat tnttag event is currently on-going and cannot be joined.");
            return;
        }

        if (profile.getSumoEvent() != null){
            sendMessage(player, "&cYou are already in a tnttag event.");
            return;
        }

        SoupPvP.getInstance().getTntTagHandler().getActiveGame().handleJoin(player);
    }
}
