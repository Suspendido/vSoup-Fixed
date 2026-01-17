package kami.gg.souppvp.events.impl.sumo.command;

import kami.gg.souppvp.events.impl.sumo.command.args.*;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SumoCommand extends Command {
    public SumoCommand(CommandManager manager) {
        super(manager, "sumo");
        this.handleArguments(
                new SumoCancelArg(manager),
                new SumoJoinArg(manager),
                new SumoHostArg(manager),
                new SumoLeaveArg(manager),
                new SumoSetSpawnArg(manager),
                new SumoTpArg(manager)
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
                "&6&lSumo Event",
                "",
                "&e/sumo host",
                "&e/sumo cancel",
                "&e/sumo join",
                "&e/sumo leave",
                "&e/sumo tp",
                "&e/sumo setspawn",
                CC.CHAT_BAR
        ));
    }
}
