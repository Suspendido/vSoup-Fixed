package kami.gg.souppvp.command.credit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CreditsSetCommand extends Command {

    public CreditsSetCommand(CommandManager manager) {
        super(
                manager,
                "setcredits"
        );
        this.setPermissible("souppvp.credits");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList(CC.translate("&cUsage: /setcredits <player> <int>"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String s = args[0];
        Profile targetProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(s);

        if (targetProfile == null) {
            sender.sendMessage(CC.translate("&cCouldn't resolve that player's name."));
            return;
        }

        int amount = Integer.parseInt(args[1]);
        targetProfile.setCredits(amount);
        targetProfile.saveProfile();
        sender.sendMessage(CC.translate("&aSuccessfully set &e" + targetProfile.getUsername() + "'s &abalance to &b" + amount + " &acredits."));
    }
}
