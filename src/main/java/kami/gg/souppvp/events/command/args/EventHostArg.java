package kami.gg.souppvp.events.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class EventHostArg extends Argument {

    public EventHostArg(CommandManager manager) {
        super(manager, Collections.singletonList("host"));
    }

    @Override
    public String usage() {
        return "<sumo|tnttag>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CC.t("&cOnly players can run this command."));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(CC.t("&cUsage: /event <sumo|tnttag> host"));
            return;
        }

        EventType eventType = parseEventType(args[0]);
        if (eventType == null) {
            sender.sendMessage(CC.t("&cInvalid event type. Use: sumo or tnttag"));
            return;
        }

        String permission = eventType.getHostPermission();
        if (!player.hasPermission(permission)) {
            sender.sendMessage(CC.t("&cYou don't have permission to host this event."));
            return;
        }

        Event activeEvent = SoupPvP.getInstance().getEventManager().getActiveEvent(eventType);
        if (activeEvent != null) {
            player.sendMessage(ChatColor.RED + "There is already an active " + eventType.getColor() + eventType.getName() + " event.");
            return;
        }

        Event event = SoupPvP.getInstance().getEventManager().createEvent(eventType, player);
        SoupPvP.getInstance().getEventManager().setActiveEvent(eventType, event);
        event.handleJoin(player);

        sender.sendMessage(CC.t("&a" + eventType.getColor() + eventType.getName() + " event created successfully!"));
        sender.sendMessage(CC.t("&aPlayers can now join with &b/event " + eventType.name().toLowerCase() + " join&a!"));
    }

    private EventType parseEventType(String type) {
        return Arrays.stream(EventType.values())
                .filter(et -> et.name().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}
