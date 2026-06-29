package kami.gg.souppvp.events.menu.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventManager;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EventButton extends Button {

    private final EventType eventType;

    public EventButton(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        EventManager event = SoupPvP.getInstance().getEventManager();

        List<String> description = eventType.getDescripction();
        if (description != null && !description.isEmpty()) {
            lore.addAll(description);
        } else {
            lore.add("&cNo description available");
        }

        lore.add("");
        lore.add("&bRewards");
        lore.add("&b┃ &b" + eventType.getWinCredits() + " credits");
        lore.add("");

        if (event.getActiveEvent(eventType) != null) {
            lore.add("&bOngoing " + eventType.getColor() + eventType.getName() + " &bEvent:");
            lore.add("&b┃ &fParticipants: &b" + event.getActiveEvent(eventType).getRemainingPlayers().size() + "&f/&b" + event.getActiveEvent(eventType).getMaxPlayers());
            lore.add(event.getActiveEvent(eventType).getState().equals(EventState.WAITING)
                    ? "&b┃ &fState: &bWaiting..."
                    : "&b┃ &fState: &bFighting"
            );
            lore.add("");

            lore.add(profile.isInEvent()
                    ? "&eYou're in this event!"
                    : "&eClick to join!"
            );
        } else {
            lore.add("&eClick to host!");
        }

        return new ItemBuilder(eventType.getMaterial())
                .name(eventType.getColor() + eventType.getName())
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Event activeEvent = SoupPvP.getInstance().getEventManager().getActiveEvent(eventType);

        if (profile.getProfileState() != ProfileState.SPAWN) {
            playFail(player);
            sendMessage(player, "&cYou can only do this at spawn.");
            return;
        }

        if (activeEvent == null) {
            if (!player.hasPermission(eventType.getHostPermission())) {
                playFail(player);
                sendMessage(player, "&cYou do not have permission to host this event.");
                return;
            }

            Event event = SoupPvP.getInstance().getEventManager().createEvent(eventType, player);
            SoupPvP.getInstance().getEventManager().setActiveEvent(eventType, event);
            event.handleJoin(player);

            playSuccess(player);
            return;
        }

        if (profile.isInEvent()) {
            playFail(player);
            return;
        }

        if (activeEvent.getState() != EventState.WAITING) {
            playFail(player);
            sendMessage(player, "&cThis event is currently on-going and cannot be joined.");
            return;
        }

        if (activeEvent.hasPlayer(player)) {
            playFail(player);
            sendMessage(player, "&cYou're already in this game!");
            return;
        }

        activeEvent.handleJoin(player);
        PlayerUtil.playSound(player, Sound.CLICK, 1.0);
    }
}
