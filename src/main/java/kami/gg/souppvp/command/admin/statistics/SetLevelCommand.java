package kami.gg.souppvp.command.admin.statistics;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.lang.Lang;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class SetLevelCommand extends Command {

    public SetLevelCommand(CommandManager manager) {
        super(manager, "setlevel");
    }

    @Override
    public List<String> aliases() {
        return List.of("setlevels", "settier");
    }

    @Override
    public List<String> usage() {
        return List.of();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String s = args[0];
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(s);

        if (profile == null) {
            sendMessage(sender, Lang.INVALID_PROFILE);
            return;
        }

        int value = Integer.parseInt(args[1]);
        profile.setTier(value);
        sendMessage(sender, Lang.SUCCESSFULLY_UPDATED);
        profile.saveProfile();
    }
}
