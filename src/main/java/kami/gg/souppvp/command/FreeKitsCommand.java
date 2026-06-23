package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FreeKitsCommand extends Command {

    public FreeKitsCommand(CommandManager manager) {
        super(
                manager,
                "togglekits"
        );
        this.setPermissible("souppvp.freekits");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("free", "freekits");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        SoupPvP.setIsFreeKitsMode(!SoupPvP.getIsFreeKitsMode());
        SoupPvP.getInstance().getConfig().set("FREE-KITS", SoupPvP.getIsFreeKitsMode());
        SoupPvP.getInstance().saveConfig();
        SoupPvP.getInstance().reloadConfig();
        String status = SoupPvP.getIsFreeKitsMode() ? "&a&lenabled" : "&c&ldisabled";
        sendMessage(player, "&aSuccessfully " + status + " &afree kits mode!");
    }
}
