package kami.gg.souppvp.events.impl.tnttag.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class TNTTagCancelArg extends Argument {

    public TNTTagCancelArg(CommandManager manager) {
        super(manager, Collections.singletonList("cancel"));
        this.setPermissible("souppvp.tnttagcancel");
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (SoupPvP.getInstance().getTntTagHandler().getActiveGame() == null) {
            sendMessage(sender, "&cThere isn't an active tnttag event.");
            return;
        }
        SoupPvP.getInstance().getTntTagHandler().getActiveGame().end();
    }
}
