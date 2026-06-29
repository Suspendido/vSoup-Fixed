package kami.gg.souppvp.events.command.args;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class EventSpecArg extends Argument {

    public EventSpecArg(CommandManager manager) {
        super(manager, Collections.singletonList("spec"));
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
            sender.sendMessage(CC.t("&cUsage: /event <sumo|tnttag> spec"));
            return;
        }

        EventType eventType = parseEventType(args[0]);
        if (eventType == null) {
            sender.sendMessage(CC.t("&cInvalid event type. Use: sumo or tnttag"));
            return;
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Event activeEvent = getActiveEvent(eventType);

        if (activeEvent == null) {
            player.sendMessage(CC.t("&cThere isn't an active " + eventType.getColor() + eventType.getName() + " event."));
            return;
        }

        if (activeEvent.hasPlayer(player)) {
            player.sendMessage(CC.t("&cYou are already in this event."));
            return;
        }

        if (profile.getProfileState() != ProfileState.SPAWN) {
            player.sendMessage(CC.t("&cYou can only spectate from spawn."));
            return;
        }

        addSpectator(eventType, player);
        player.sendMessage(CC.t("&aYou are now spectating the " + eventType.getColor() + eventType.getName() + " event."));
    }

    private EventType parseEventType(String type) {
        return Arrays.stream(EventType.values())
                .filter(et -> et.name().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }

    private Event getActiveEvent(EventType eventType) {
        return SoupPvP.getInstance().getEventManager().getActiveEvent(eventType);
    }

    private void addSpectator(EventType eventType, Player player) {
        switch (eventType) {
            case SUMO -> SoupPvP.getInstance().getSumoHandler().getActiveSumo().addSpectator(player);
            case TNTTAG -> SoupPvP.getInstance().getTntTagHandler().getActiveGame().addSpectator(player);
        }
    }
}
