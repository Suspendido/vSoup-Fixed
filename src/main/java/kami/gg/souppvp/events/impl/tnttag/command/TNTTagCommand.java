package kami.gg.souppvp.events.impl.tnttag.command;

import kami.gg.souppvp.events.impl.tnttag.command.args.*;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TNTTagCommand extends Command {

    public TNTTagCommand(CommandManager manager) {
        super(manager, "tnttag");
        this.handleArguments(
                new TNTTagJoinArg(manager),
                new TNTTagLeaveArg(manager),
                new TNTTagHostArg(manager),
                new TNTTagCancelArg(manager),
                new TNTTagSetSpawnArg(manager),
                new TNTTagTPArg(manager),
                new TNTTagSpecArg(manager)
        );
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return CC.translate(Arrays.asList(
                CC.CHAT_BAR,
                "&4&lTNTTag Event",
                "",
                "&f/tnttag host",
                "&f/tnttag cancel",
                "&f/tnttag join",
                "&f/tnttag leave",
                "&f/tnttag tp",
                "&f/tnttag setspawn",
                CC.CHAT_BAR
        ));
    }
}
