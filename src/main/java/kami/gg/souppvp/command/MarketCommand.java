package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.menu.KitsBuyMenu;
import kami.gg.souppvp.kit.menu.KitsSelectMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MarketCommand extends Command {

    public MarketCommand(CommandManager manager) {
        super(manager, "market");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("shop", "store");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CC.translate("&cOnly players can run this command."));
            return;
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        boolean isInSpawn = SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player) && profile.getProfileState() == ProfileState.SPAWN;

        if (!isInSpawn) {
            sender.sendMessage(CC.translate("&cYou cannot open the kits menu while not being on spawn!"));
            return;
        }

        new KitsBuyMenu().openMenu(player);
    }
}
