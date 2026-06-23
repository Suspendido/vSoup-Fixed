package kami.gg.souppvp.events.impl.sumo.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SumoLeaveArg extends Argument {

    public SumoLeaveArg(CommandManager manager) {
        super(manager, Collections.singletonList("leave"));
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

        if (activeSumo == null) {
            player.sendMessage(CC.t("&cThere isn't an active sumo event."));
            return;
        }

        if (profile.getSumoEvent() == null || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(CC.t("&cYou are not apart of the active sumo event."));
            return;
        }

        SoupPvP.getInstance().getSumoHandler().getActiveSumo().handleLeave(player);
    }
}
