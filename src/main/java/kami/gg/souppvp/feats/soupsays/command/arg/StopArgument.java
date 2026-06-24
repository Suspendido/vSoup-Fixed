package kami.gg.souppvp.feats.soupsays.command.arg;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.soupsays.SoupSaysManager;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class StopArgument extends Argument {

    public StopArgument(CommandManager manager) {
        super(manager, List.of("stop", "deactivate"));
    }

    @Override
    public String usage() {
        return "&cUsage: /soupsays stop";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        SoupSaysManager manager = SoupPvP.getInstance().getSoupSaysManager();
        manager.getActiveTask().deactivate(null);
        sendMessage(sender, "&aDeactivated active task");
    }
}
