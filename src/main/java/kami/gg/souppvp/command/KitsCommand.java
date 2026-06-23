package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
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

public class KitsCommand extends Command {
    public KitsCommand(CommandManager manager) {
        super(manager, "kits");
    }

    @Override
    public List<String> aliases() {
        return Arrays.asList("kit", "gkit", "vkit", "zkit");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cOnly players can run this command.");
            return;
        }
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        boolean isInSpawn = SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player) && profile.getProfileState() == ProfileState.SPAWN;

        if (!isInSpawn) {
            sendMessage(player, "&cYou cannot open the kits menu while not being on spawn!");
            return;
        }

        new KitsSelectMenu().openMenu(player);
    }
}
