package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.menus.stats.StatisticsMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StatisticsCommand extends Command {

    public StatisticsCommand(CommandManager manager) {
        super(manager, "statistics");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("statistic", "stats", "stat");
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList("&cUsage: /stats <player>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cThis command can only be used by players.");
            return;
        }

        String targetName;
        if (args.length < 1) {
            targetName = player.getName();
        } else {
            targetName = args[0];
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(targetName);

        if (profile == null) {
            sendMessage(player, "&cCouldn't resolve that player's name.");
            return;
        }

        new StatisticsMenu(player, profile).open();
    }
}
