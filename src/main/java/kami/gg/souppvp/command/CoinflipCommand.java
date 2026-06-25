package kami.gg.souppvp.command;

import kami.gg.souppvp.coinflip.menu.CoinFlipMenu;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CoinflipCommand extends Command {

    public CoinflipCommand(CommandManager manager) {
        super(
                manager,
                "coinflip"
        );
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("wager", "cf");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new CoinFlipMenu((Player) sender).open();
    }
}
