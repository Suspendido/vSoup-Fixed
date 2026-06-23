package kami.gg.souppvp.events.impl.sumo.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class SumoCancelArg extends Argument {

    public SumoCancelArg(CommandManager manager) {
        super(manager, Collections.singletonList("cancel"));
        this.setPermissible("souppvp.sumocancel");
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (SoupPvP.getInstance().getSumoHandler().getActiveSumo() == null) {
            sender.sendMessage(CC.t("&cThere isn't an active sumo event."));
            return;
        }
        SoupPvP.getInstance().getSumoHandler().getActiveSumo().end();
    }
}
