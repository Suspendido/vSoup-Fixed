package kami.gg.souppvp.events.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class EventLeaveArg extends Argument {

    public EventLeaveArg(CommandManager manager) {
        super(manager, Collections.singletonList("leave"));
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
            sender.sendMessage(CC.t("&cUsage: /event <sumo|tnttag> leave"));
            return;
        }

        EventType eventType = parseEventType(args[0]);
        if (eventType == null) {
            sender.sendMessage(CC.t("&cInvalid event type. Use: sumo or tnttag"));
            return;
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Event activeEvent = SoupPvP.getInstance().getEventManager().getActiveEvent(eventType);

        if (activeEvent == null) {
            player.sendMessage(CC.t("&cThere isn't an active " + eventType.getColor() + eventType.getName() + " event."));
            return;
        }

        if (!activeEvent.hasPlayer(player)) {
            player.sendMessage(CC.t("&cYou are not in this event."));
            return;
        }

        activeEvent.handleLeave(player);
        player.sendMessage(CC.t("&aYou left the " + eventType.getColor() + eventType.getName() + " event."));
    }

    private EventType parseEventType(String type) {
        return Arrays.stream(EventType.values())
                .filter(et -> et.name().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}
