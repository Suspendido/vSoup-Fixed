package kami.gg.souppvp.feats.leaderboard.command;

import kami.gg.souppvp.feats.leaderboard.menu.LeaderboardMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class LeaderboardCommand extends Command {

    public LeaderboardCommand(CommandManager manager) {
        super(manager, "leaderboard");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("lb", "top");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cThis command is only for players.");
            return;
        }

        new LeaderboardMenu().openMenu(player);
    }
}