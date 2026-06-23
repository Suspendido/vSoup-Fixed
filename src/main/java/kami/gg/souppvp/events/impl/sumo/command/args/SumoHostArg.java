package kami.gg.souppvp.events.impl.sumo.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SumoHostArg extends Argument {

    public SumoHostArg(CommandManager manager) {
        super(manager, Collections.singletonList("host"));
        this.setPermissible("souppvp.sumohost");
    }

    @Override
    public String usage() {
        return "";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CC.t("&cOnly players can run this command."));
            return;
        }

        if (SoupPvP.getInstance().getSumoHandler().getActiveSumo() != null) {
            player.sendMessage(ChatColor.RED + "There is already an active event.");
            return;
        }

        SoupPvP.getInstance().getSumoHandler().setActiveSumo(new Sumo(player));
        SoupPvP.getInstance().getSumoHandler().getActiveSumo().handleJoin(player);
    }
}
