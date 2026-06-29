package kami.gg.souppvp.command.spawn;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class OPLeaveCommand extends Command {

    public OPLeaveCommand(CommandManager manager) {
        super(
                manager,
                "opspawn"
        );
        this.setPermissible("souppvp.opleave");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("opleave");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.getActiveEvent() == null) {
            PlayerUtil.resetPlayer(player);
        } else {
            player.sendMessage(CC.t("&cYou're currently in an event."));
        }
    }
}
