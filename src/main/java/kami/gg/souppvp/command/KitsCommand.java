package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class KitsCommand extends Command {
    public KitsCommand(CommandManager manager, String name) {
        super(manager, name);
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList(CC.translate("&cUsage: /setdeaths <profile> <int>"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String s = args[0];
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(s);

        int value = Integer.parseInt(args[1]);
        profile.setDeaths(value);
        sender.sendMessage(CC.translate("&aSuccessfully updated!"));
        profile.saveProfile();
    }
}
