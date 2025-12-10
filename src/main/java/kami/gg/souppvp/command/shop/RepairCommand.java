package kami.gg.souppvp.command.shop;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
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
            sender.sendMessage(CC.translate("&cYou may not repair whilst in Juggernaut."));
            return;
        }

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)){
            PlayerUtil.playSound(player, Sound.DIG_GRASS);
            player.sendMessage(CC.translate("&cYou can't do this in spawn."));
        } else {
            if (profile.getCredits() >= 150){
                PlayerUtil.playSound(player, Sound.NOTE_PIANO);
                profile.setCredits(profile.getCredits() - 150);
                PlayerUtil.repairPlayer(player);
                player.sendMessage(CC.translate("&aSuccessfully bought the &dRepair Durability &afeature."));
            } else {
                PlayerUtil.playSound(player, Sound.DIG_GRASS);
            }
        }
    }
}
