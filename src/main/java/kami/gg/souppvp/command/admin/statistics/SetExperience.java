package kami.gg.souppvp.command.admin.statistics;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetExperience extends Command {

    public SetExperience(CommandManager manager) {
        super(manager, "setexperience");
        this.setPermissible("souppvp.setexperience");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList(CC.t("&cUsage: /setexperience <profile> <int>"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String s = args[0];
        Player player = Bukkit.getPlayer(s);
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(s);

        if (player == null || profile == null) {
            sendMessage(player, "&cCouldn't resolve that player's name.");
            return;
        }

        int value = Integer.parseInt(args[1]);
        profile.setExperiences(value);
        sendMessage(player, "&aSuccessfully updated!");
        profile.saveProfile();
    }
}
