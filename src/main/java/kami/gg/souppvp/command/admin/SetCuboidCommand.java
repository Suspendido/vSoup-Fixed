package kami.gg.souppvp.command.admin;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.Cuboid;
import kami.gg.souppvp.util.LocationUtil;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetCuboidCommand extends Command {

    public SetCuboidCommand(CommandManager manager) {
        super(
                manager,
                "setcuboid"
        );
        this.setPermissible("souppvp.cuboid");
    }

    @Override
    public List<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public List<String> usage() {
        return Collections.singletonList(CC.t("&cUsage: /setcuboid <a/b>"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String position = args[0];
        Player player = (Player) sender;
        if (position.equalsIgnoreCase("a")){
            SoupPvP.getInstance().getSpawnHandler().setA(player.getLocation());
            SoupPvP.getInstance().getSpawnHandler().setCuboid(new Cuboid(SoupPvP.getInstance().getSpawnHandler().getA(), SoupPvP.getInstance().getSpawnHandler().getB()));
            SoupPvP.getInstance().getConfig().set("SPAWN.LOCATION-A", LocationUtil.convertLocationToString(SoupPvP.getInstance().getSpawnHandler().getA()));
            SoupPvP.getInstance().saveConfig();
            SoupPvP.getInstance().reloadConfig();
            player.sendMessage(CC.t("&aSuccessfully updated the spawn's location a."));
        } else if (position.equalsIgnoreCase("b")) {
            SoupPvP.getInstance().getSpawnHandler().setB(player.getLocation());
            SoupPvP.getInstance().getSpawnHandler().setCuboid(new Cuboid(SoupPvP.getInstance().getSpawnHandler().getA(), SoupPvP.getInstance().getSpawnHandler().getB()));
            SoupPvP.getInstance().getConfig().set("SPAWN.LOCATION-B", LocationUtil.convertLocationToString(SoupPvP.getInstance().getSpawnHandler().getB()));
            SoupPvP.getInstance().saveConfig();
            SoupPvP.getInstance().reloadConfig();
            player.sendMessage(CC.t("&aSuccessfully updated the spawn's location b."));
        } else {
            player.sendMessage(CC.t("&cAvailable Positions: a, b"));
        }
    }
}
