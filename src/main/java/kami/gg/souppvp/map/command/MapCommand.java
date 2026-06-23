package kami.gg.souppvp.map.command;

import kami.gg.souppvp.map.MapManager;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapCommand extends Command {

    private final MapManager mapManager;

    public MapCommand(CommandManager manager) {
        super(manager, "map");
        this.mapManager = getInstance().getMapManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            this.sendMessage(sender, "&cYou cannot use this command.");
            return;
        }

        if (args.length == 0) {
            sendUsage(player);
            return;
        }

        if (!player.hasPermission("hcf.command.map")) {
            sendMessage(player, "&cYou dont have permissions to use this command.");
            return;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "setstart":
                mapManager.setStartDate();
                player.sendMessage(CC.t("&aMap start date set to now."));
                break;

            case "setduration":
                if (args.length < 2) {
                    player.sendMessage(CC.t("&cUsage: /map setduration <duration> (e.g., 1d12h)"));
                    return;
                }
                String duration = args[1];
                mapManager.setDuration(duration);
                player.sendMessage(CC.t("&aMap duration set to " + duration + "."));
                break;
            default:
                sendUsage(player);
                break;
        }
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return Arrays.asList(
                "&cUsage: /map <setstart|setduration>",
                "&c  - setstart: Sets the map start date to now",
                "&c  - setduration <duration>: Sets the map duration (e.g., 1d12h)"
        );
    }
}