package kami.gg.souppvp.feats.soupsays.command.arg;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.soupsays.SoupSaysManager;
import kami.gg.souppvp.feats.soupsays.Tasks;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class StartArgument extends Argument {

    public StartArgument(CommandManager manager) {
        super(manager, List.of("start", "activate"));
    }

    @Override
    public String usage() {
        return "&cUsage: /soupsays start <random|taskID>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        SoupSaysManager soupSaysManager = SoupPvP.getInstance().getSoupSaysManager();

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String taskName = args[0];

        if (taskName.equalsIgnoreCase("random")) {
            if (soupSaysManager.getActiveTask() != null) {
                sender.sendMessage(CC.t("&cThere is already an active task!"));
                return;
            }
            soupSaysManager.activateRandom();
            return;
        }

        Tasks task = soupSaysManager.findTask(taskName);

        if (task == null) {
            sender.sendMessage(CC.t("&cTask not found: " + taskName));
            return;
        }

        if (soupSaysManager.getActiveTask() != null) {
            sender.sendMessage(CC.t("&cThere is already an active task!"));
            return;
        }

        soupSaysManager.setActiveTask(task);
        task.activate();
    }
}
