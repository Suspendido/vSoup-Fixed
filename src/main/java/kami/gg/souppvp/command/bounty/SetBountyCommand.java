package kami.gg.souppvp.command.bounty;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * @author hieu
 * @date 26/09/2023
 */

public class SetBountyCommand extends Command {

    public SetBountyCommand(CommandManager manager) {
        super(
                manager,
                "setbounty"
        );
        this.setPermissible("souppvp.setbounty");
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList("&cUsage: /setbounty <profile> <int>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }
        Player player = (Player) sender;
        String s = args[0];
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(s);
        if (profile == null){
            sender.sendMessage(CC.translate("&cCouldn't resolve that player's name."));
            return;
        }

        int value = Integer.parseInt(args[1]);
        profile.setBounty(value);
        profile.saveProfile();
        sender.sendMessage(CC.translate("&aSuccessfully updated!"));
    }
}
