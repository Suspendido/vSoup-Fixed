package kami.gg.souppvp.command.spawn;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SpawnCommand extends Command {

    public SpawnCommand(CommandManager manager) {
        super(manager, "spawn");
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("leave");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.isCombatTagged()) {
            player.sendMessage(CC.t("&cYou're currently combat-tagged."));
            return;
        }

        if (profile.getActiveEvent() != null) {
            player.sendMessage(CC.t("&cYou're currently in an event."));
            return;
        }

        PlayerUtil.resetPlayer(player);
    }

}
