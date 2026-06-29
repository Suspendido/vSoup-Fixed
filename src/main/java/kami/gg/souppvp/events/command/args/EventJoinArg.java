package kami.gg.souppvp.events.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class EventJoinArg extends Argument {

    public EventJoinArg(CommandManager manager) {
        super(manager, Collections.singletonList("join"));
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
            sender.sendMessage(CC.t("&cUsage: /event <sumo|tnttag> join"));
            return;
        }

        EventType eventType = parseEventType(args[0]);
        if (eventType == null) {
            sender.sendMessage(CC.t("&cInvalid event type. Use: sumo or tnttag"));
            return;
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Event activeEvent = SoupPvP.getInstance().getEventManager().getActiveEvent(eventType);

        if (!profile.getProfileState().equals(ProfileState.SPAWN)) {
            player.sendMessage(CC.t("&cYou cannot join the event right now. You need to be at spawn."));
            return;
        }

        if (activeEvent == null) {
            player.sendMessage(CC.t("&cThere isn't an active " + eventType.getColor() + eventType.getName() + " event."));
            return;
        }

        if (activeEvent.getState() != EventState.WAITING) {
            player.sendMessage(CC.t("&cThat " + eventType.getColor() + eventType.getName() + " event is currently on-going and cannot be joined."));
            return;
        }

        if (profile.isInEvent()) {
            player.sendMessage(CC.t("&cYou are already in an event."));
            return;
        }

        activeEvent.handleJoin(player);
    }

    private EventType parseEventType(String type) {
        return Arrays.stream(EventType.values())
                .filter(et -> et.name().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}
