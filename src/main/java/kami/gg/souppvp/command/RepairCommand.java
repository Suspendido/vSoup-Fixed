package kami.gg.souppvp.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class RepairCommand extends Command {

    public RepairCommand(CommandManager manager) {
        super(
                manager,
                "repair"
        );
    }

    @Override
    public List<String> aliases() {
        return Collections.singletonList("fix");
    }

    @Override
    public List<String> usage() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (profile.isJuggernaut()) {
            sendMessage(player, "&cYou may not repair whilst in Juggernaut.");
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            PlayerUtil.playSound(player, Sound.DIG_GRASS, 1.0);
            sendMessage(player, "&cYou can't do this in spawn.");
            return;
        }

        if (profile.getCredits() < 150) {
            sendMessage(player, "&cInsufficient credits! You're " + (profile.getCredits() - 150) + " credits short.");
            PlayerUtil.playSound(player, Sound.DIG_GRASS, 1.0);
            return;
        }

        profile.setCredits(profile.getCredits() - 150);
        PlayerUtil.playSound(player, Sound.NOTE_PIANO, 1.0);
        PlayerUtil.repairPlayer(player);
        sendMessage(player, "&aSuccessfully bought the &dRepair Durability &afeature.");
    }
}
