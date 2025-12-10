package kami.gg.souppvp.command.spawn;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.TimeUtil;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) {
            player.sendMessage(CC.translate("&cYou can't do this in spawn."));
            return;
        }

        if (profile.isCombatTagged()) {
            player.sendMessage(CC.translate("&cYou're currently combat-tagged."));
            return;
        }

        if (profile.getSumoEvent() != null) {
            player.sendMessage(CC.translate("&cYou're currently in a sumo event."));
            return;
        }

        if (profile.isTeleportingToSpawn()) {
            player.sendMessage(CC.translate("&cYou're already teleporting."));
            return;
        }

        profile.addSpawnTeleportation();

        new BukkitRunnable() {

            int seconds = 6;

            @Override
            public void run() {
                if (!profile.isTeleportingToSpawn()) {
                    cancel();
                    return;
                }

                seconds--;

                if (seconds > 0) {
                    player.sendMessage(CC.translate("&3Spawn teleportation in &b" + TimeUtil.convertToHhMmSs((long) seconds) + "&3..."));
                    PlayerUtil.playSound(player, Sound.CHICKEN_EGG_POP);
                    return;
                }

                profile.removeSpawnTeleportation();
                PlayerUtil.resetPlayer(player);
                cancel();
            }

        }.runTaskTimerAsynchronously(SoupPvP.getInstance(), 0L, 20L);
    }

}
