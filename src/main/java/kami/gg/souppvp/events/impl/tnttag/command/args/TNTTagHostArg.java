package kami.gg.souppvp.events.impl.tnttag.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.impl.tnttag.task.TNTTagStartTask;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TNTTagHostArg extends Argument {

    public TNTTagHostArg(CommandManager manager) {
        super(manager, Collections.singletonList("host"));
        this.setPermissible("souppvp.tnttaghost");
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cOnly players can run this command.");
            return;
        }

        if (SoupPvP.getInstance().getTntTagHandler().getActiveGame() != null) {
            sendMessage(player, "&cThere is already an active TNTTag event.");
            return;
        }

        TNTTagGame game = new TNTTagGame(player);
        SoupPvP.getInstance().getTntTagHandler().setActiveGame(game);

        game.handleJoin(player);
        game.setEventTask(new TNTTagStartTask(game));

        sendMessage(player, "&aTNTTag Event created successfully!");
        sendMessage(player, "&aPlayers can now join with &b/tnttag join&a!");
    }
}