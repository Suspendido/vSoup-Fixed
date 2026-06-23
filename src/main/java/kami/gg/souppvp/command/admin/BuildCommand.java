package kami.gg.souppvp.command.admin;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collections;
import java.util.List;

/**
 * @author hieu
 * @date 28/09/2023
 */

public class BuildCommand extends Command {

    public BuildCommand(CommandManager manager) {
        super(
                manager,
                "build"
        );
        this.setPermissible("souppvp.build");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (player.hasMetadata("build")) {
            player.removeMetadata("build", SoupPvP.getInstance());
            player.sendMessage(CC.t("&cYou are no longer in builder mode."));
        } else {
            player.setMetadata("build", new FixedMetadataValue(SoupPvP.getInstance(), "build"));
            player.sendMessage(CC.t("&aYou are now in builder mode."));
            player.setGameMode(GameMode.CREATIVE);
        }
    }
}
