package kami.gg.souppvp.events.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.LocationUtil;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class EventSetSpawnArg extends Argument {

    public EventSetSpawnArg(CommandManager manager) {
        super(manager, Collections.singletonList("setspawn"));
    }

    @Override
    public String usage() {
        return "<sumo|tnttag> [spectator|spawn]";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CC.t("&cOnly players can run this command."));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(CC.t("&cUsage: /event <sumo|tnttag> setspawn [spectator|spawn]"));
            return;
        }

        EventType eventType = parseEventType(args[0]);
        if (eventType == null) {
            sender.sendMessage(CC.t("&cInvalid event type. Use: sumo or tnttag"));
            return;
        }

        String permission = eventType.getHostPermission();
        if (!player.hasPermission(permission)) {
            sender.sendMessage(CC.t("&cYou don't have permission to set spawn for this event."));
            return;
        }

        String spawnType = args.length >= 3 ? args[2].toLowerCase() : "spectator";
        Location location = player.getLocation();

        setSpawn(eventType, spawnType, location);
        player.sendMessage(CC.t("&a" + eventType.getColor() + eventType.getName() + " " + spawnType + " spawn set to your location."));
    }

    private EventType parseEventType(String type) {
        return Arrays.stream(EventType.values())
                .filter(et -> et.name().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }

    private void setSpawn(EventType eventType, String spawnType, Location location) {
        String configPath = "EVENTS." + eventType.name().toUpperCase() + "." + spawnType.toUpperCase() + "-SPAWN";
        SoupPvP.getInstance().getConfig().set(configPath, LocationUtil.serialize(location));
        SoupPvP.getInstance().saveConfig();
        SoupPvP.getInstance().reloadConfig();

        // Also update handler
        switch (eventType) {
            case SUMO -> {
                switch (spawnType) {
                    case "spectator" -> SoupPvP.getInstance().getSumoHandler().setSpectatorSpawn(location);
                    case "spawna" -> SoupPvP.getInstance().getSumoHandler().setSpawnA(location);
                    case "spawnb" -> SoupPvP.getInstance().getSumoHandler().setSpawnB(location);
                }
            }
            case TNTTAG -> {
                switch (spawnType) {
                    case "spectator" -> SoupPvP.getInstance().getTntTagHandler().setSpectatorSpawn(location);
                    case "spawn" -> SoupPvP.getInstance().getTntTagHandler().setEventSpawn(location);
                }
            }
        }
    }
}
