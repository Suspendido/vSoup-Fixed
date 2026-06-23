package kami.gg.souppvp.events.impl.sumo.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.impl.sumo.SumoState;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SumoJoinArg extends Argument {

    public SumoJoinArg(CommandManager manager) {
        super(manager, Collections.singletonList("join"));
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

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Sumo activeSumo = SoupPvP.getInstance().getSumoHandler().getActiveSumo();

        if (!profile.getProfileState().equals(ProfileState.SPAWN)) {
            player.sendMessage(CC.t("&cYou cannot join the sumo event right now. You need to be at spawn."));
            return;
        }

        if (activeSumo == null) {
            player.sendMessage(CC.t("&cThere isn't an active sumo event."));
            return;
        }

        if (activeSumo.getState() != SumoState.WAITING) {
            player.sendMessage(CC.t("&cThat sumo event is currently on-going and cannot be joined."));
            return;
        }

        if (profile.getSumoEvent() != null){
            player.sendMessage(CC.t("&cYou are already in a sumo event."));
            return;
        }

        SoupPvP.getInstance().getSumoHandler().getActiveSumo().handleJoin(player);
    }
}
