package kami.gg.souppvp.events.command;

import kami.gg.souppvp.events.command.args.*;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EventCommand extends Command {

    public EventCommand(CommandManager manager) {
        super(manager, "event");
        this.handleArguments(
                new EventHostArg(manager),
                new EventJoinArg(manager),
                new EventLeaveArg(manager),
                new EventCancelArg(manager),
                new EventSetSpawnArg(manager),
                new EventTpArg(manager),
                new EventSpecArg(manager)
        );
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return CC.t(Arrays.asList(
                CC.CHAT_BAR,
                "&6&lEvent Command",
                "",
                "&e/event <name> host",
                "&e/event <name> cancel",
                "&e/event <name> join",
                "&e/event <name> leave",
                "&e/event <name> tp",
                "&e/event <name> setspawn",
                "&e/event <name> spec",
                CC.CHAT_BAR
        ));
    }
}
