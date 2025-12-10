package kami.gg.souppvp.command.admin.statistics;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * @author hieu
 * @date 26/09/2023
 */

public class SetCreditsCommand extends Command {

    public SetCreditsCommand(CommandManager manager) {
        super(
                manager,
                "setcredits"
        );
        this.setPermissible("souppvp.setcredits");
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList("&cUsage: /setcredits <profile> <int>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String s = args[0];
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(s);
        if (profile == null){
            sender.sendMessage(CC.translate("&cCouldn't resolve that player's name."));
            return;
        }

        int value = Integer.parseInt(args[1]);
        profile.setCredits(value);
        sender.sendMessage(CC.translate("&aSuccessfully updated!"));
        profile.saveProfile();
    }
}