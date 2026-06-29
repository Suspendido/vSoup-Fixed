package kami.gg.souppvp.events;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.impl.sumo.task.SumoStartTask;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.impl.tnttag.task.TNTTagStartTask;
import kami.gg.souppvp.events.util.EventGamePlayer;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.EventUtil;
import kami.gg.souppvp.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    
    private final SoupPvP plugin;
    
    public EventManager(SoupPvP plugin) {
        this.plugin = plugin;
    }

    public Event getActiveEvent(EventType eventType) {
        return switch (eventType) {
            case SUMO -> plugin.getSumoHandler().getActiveSumo();
            case TNTTAG -> plugin.getTntTagHandler().getActiveGame();
        };
    }

    public void setActiveEvent(EventType eventType, Event event) {
        switch (eventType) {
            case SUMO -> plugin.getSumoHandler().setActiveSumo((Sumo) event);
            case TNTTAG -> plugin.getTntTagHandler().setActiveGame((TNTTagGame) event);
        }
    }

    public Event createEvent(EventType eventType, Player player) {
        return switch (eventType) {
            case SUMO -> {
                Sumo sumo = new Sumo(player);
                sumo.setEventTask(new SumoStartTask(sumo));
                yield sumo;
            }
            case TNTTAG -> {
                TNTTagGame game = new TNTTagGame(player);
                game.setEventTask(new TNTTagStartTask(game));
                yield game;
            }
        };
    }

    public List<Event> getActiveEvents() {
        List<Event> events = new ArrayList<>();
        Sumo sumo = plugin.getSumoHandler().getActiveSumo();
        TNTTagGame tnt = plugin.getTntTagHandler().getActiveGame();

        if (sumo != null) events.add(sumo);
        if (tnt != null) events.add(tnt);
        
        return events;
    }

    public List<Event> getWaitingEvents() {
        List<Event> waiting = new ArrayList<>();
        for (Event event : getActiveEvents()) {
            if (event.getState() == EventState.WAITING) {
                waiting.add(event);
            }
        }
        return waiting;
    }

    public boolean isEventInWinState(Event event) {
        if (event == null) return false;
        if (event.getState() == EventState.WAITING) return false;
        return event.getRemainingPlayers().size() == 1;
    }

    public Player getEventWinnerIfFinished(Event event) {
        if (!isEventInWinState(event)) return null;
        return event.getWinner();
    }

    public Event getPlayerEvent(Player player) {
        for (Event event : getActiveEvents()) {
            if (event.hasPlayer(player)) {
                return event;
            }
        }
        return null;
    }

    public void handleEventRewards(Event event) {
        Player winner = event.getWinner();
        
        if (winner == null) {
            Bukkit.broadcastMessage(CC.t("&cThe " + event.getEventName() + " Event has been cancelled."));
        } else {
            Profile profile = plugin.getProfilesHandler().getProfileByUUID(winner.getUniqueId());
            profile.setEventsWon(profile.getEventsWon() + 1);
            profile.setCredits(profile.getCredits() + 100);
            Bukkit.broadcastMessage(CC.t("&b" + winner.getName() + " &7has won the &b" + event.getEventName() + " &7Event!"));
        }
    }

    public void handleEventCleanup(Event event) {
        for (Player player : event.getRemainingPlayers()) {
            Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());
            profile.setProfileState(ProfileState.SPAWN);
            PlayerUtil.resetPlayer(player);
        }
    }

    public void handleEventJoin(Player player, Location spawnLocation) {
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setProfileState(ProfileState.IN_EVENT);
        EventUtil.resetPlayer(player);
        player.teleport(spawnLocation);
    }

    public void handleEventLeave(Player player) {
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setProfileState(ProfileState.SPAWN);
        PlayerUtil.resetPlayer(player);
    }

    public void broadcastEventMessage(Event event, String message) {
        for (Player player : event.getRemainingPlayers()) {
            player.sendMessage(CC.t(message));
        }
    }

    public void handleEventEnd(Event event) {
        switch (event.getType()) {
            case SUMO -> handleSumoEnd((Sumo) event);
            case TNTTAG -> handleTNTTagEnd((TNTTagGame) event);
        }
    }

    private void handleSumoEnd(Sumo sumo) {
        plugin.getSumoHandler().setActiveSumo(null);
        sumo.setEventTask(null);

        handleEventRewards(sumo);
        handleEventCleanup(sumo);

        for (EventGamePlayer gamePlayer : sumo.getEventPlayers().values()) {
            Player player = gamePlayer.getPlayer();
            if (player != null) {
                // Profile cleanup handled by removeSpectator
            }
        }
        sumo.getSpectatorsList().forEach(sumo::removeSpectator);
    }

    private void handleTNTTagEnd(TNTTagGame tnt) {
        plugin.getTntTagHandler().setActiveGame(null);
        tnt.setEventTask(null);
        Player winner = tnt.getWinner();

        handleEventRewards(tnt);

        if (winner == null) {
            handleEventCleanup(tnt);
            tnt.getSpectatorsList().forEach(tnt::removeSpectator);
        } else {
            tnt.playVictoryEffects(winner);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                handleEventCleanup(tnt);
                tnt.getSpectatorsList().forEach(tnt::removeSpectator);
            }, 60L);
        }
    }
}
