package kami.gg.souppvp.command.credit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.lang.Lang;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CreditsAddCommand extends Command {

    public CreditsAddCommand(CommandManager manager) {
        super(
                manager,
                "addcredits"
        );
        this.setPermissible("souppvp.credits");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList(CC.t("&cUsage: /addcredits <player> <int>"));
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
            sendMessage(sender, Lang.INVALID_PROFILE);
            return;
        }

        int amount = Integer.parseInt(args[1]);
        targetProfile.setCredits(targetProfile.getCredits() + amount);
        targetProfile.saveProfile();
        sender.sendMessage(CC.t("&aSuccessfully add &e" + amount + "&a credits to &e" + targetProfile.getUsername() + "'s &abalance."));
    }
}
