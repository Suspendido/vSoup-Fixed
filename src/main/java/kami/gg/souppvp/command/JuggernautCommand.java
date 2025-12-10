package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.juggernaut.Juggernaut;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * @author hieu
 * @date 10/06/2023
 */
public class JuggernautCommand extends Command {

    public JuggernautCommand(CommandManager manager) {
        super(
                manager,
                "juggernaut"
        );
        this.setPermissible("souppvp.juggernaut");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList(CC.translate("&cUsage: /juggernaut <player>"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CC.translate("&cOnly players can run this command."));
            return;
        }

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(CC.translate("&cCouldn't find that player online."));
            return;
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());

        if (profile == null) {
            sender.sendMessage(CC.translate("&cThat player's profile could not be loaded."));
            return;
        }

        if (profile.getProfileState() != ProfileState.SPAWN) {
            sender.sendMessage(CC.translate("&cThat player must be in spawn first."));
            return;
        }

        Juggernaut.setJuggernaut(target);
        sender.sendMessage(CC.translate("&aYou set &f" + target.getName() + " &aas the Juggernaut."));
        target.sendMessage(CC.translate("&eYou have been chosen as the Juggernaut!"));
    }
}
