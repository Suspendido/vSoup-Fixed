package kami.gg.souppvp.feats.soupsays.command;

import kami.gg.souppvp.feats.soupsays.command.arg.*;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;

import java.util.Arrays;
import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class SoupSaysCommand extends Command {

    public SoupSaysCommand(CommandManager manager) {
        super(manager, "soupsays");
        this.setPermissible("souppvp.soupsays");
        this.handleArguments(
                new StartArgument(manager),
                new StopArgument(manager)
        );
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public List<String> usage() {
        return Arrays.asList(
                CC.MENU_BAR,
                "&c/soupsays start &7- &fStart a task",
                "&c/soupsays stop &7- &fEnd a task",
                CC.MENU_BAR
        );
    }
}
