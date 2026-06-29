package kami.gg.souppvp.events.impl.sumo;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventManager;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.util.EventGamePlayer;
import kami.gg.souppvp.events.util.EventPlayerState;
import kami.gg.souppvp.events.impl.sumo.task.SumoTask;
import kami.gg.souppvp.events.util.task.EventTask;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.*;
import kami.gg.souppvp.util.fanciful.FancyMessage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class Sumo implements Event {

	public static String EVENT_PREFIX = CC.t("");

	private String name;
	private EventState state = EventState.WAITING;
	private EventTask eventTask;
	private PlayerSnapshot host;
	private LinkedHashMap<UUID, EventGamePlayer> eventPlayers = new LinkedHashMap<>();
	private List<UUID> spectators = new ArrayList<>();
    private Cooldown cooldown;
    private EventGamePlayer roundPlayerA;
    private EventGamePlayer roundPlayerB;
    private EventManager eventManager;

    private int maxPlayers;
    private int totalPlayers;
    private long roundStart;

	public Sumo(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.eventManager = SoupPvP.getInstance().getEventManager();
		this.maxPlayers = 100;
	}

	public void setEventTask(EventTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(SoupPvP.getInstance(), 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == EventState.WAITING;
	}

	public boolean isFighting() {
		return state == EventState.ROUND_FIGHTING;
	}

    @Override
	public EventState getState() {
		return state;
	}

	@Override
	public String getEventName() {
		return "Sumo";
	}

	@Override
	public EventType getType() {
		return EventType.SUMO;
	}

	@Override
	public boolean hasPlayer(Player player) {
		return eventPlayers.containsKey(player.getUniqueId()) || spectators.contains(player.getUniqueId());
	}

	public EventGamePlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (EventGamePlayer EventGamePlayer : eventPlayers.values()) {
			Player player = EventGamePlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (EventGamePlayer EventGamePlayer : eventPlayers.values()) {
			if (EventGamePlayer.getState() == EventPlayerState.WAITING || EventGamePlayer.getState() == EventPlayerState.WINNER) {
				Player player = EventGamePlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		eventPlayers.put(player.getUniqueId(), new EventGamePlayer(player));
		
		// Usar EventManager para lógica común de join
		eventManager.handleEventJoin(player, SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn().add(0.5, 0, 0.5));
		
		// Mensaje específico de Sumo
		broadcastMessage(CC.t("&b" + player.getName() + " &7has joined the &bSumo &7Event! &f(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")"));
	}

	public void handleLeave(Player player) {
		if (isFighting(player.getUniqueId())) {
			handleDeath(player);
		}
		eventPlayers.remove(player.getUniqueId());
		
		// Usar EventManager para lógica común de leave
		eventManager.handleEventLeave(player);
		
		if (state == EventState.WAITING) {
			broadcastMessage(CC.t("&b" + player.getName() + " &7has left the &bSumo &7Event! &f(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")"));
		}
	}

    @Override
    public boolean hasRounds() {
        return true;
    }

    public List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void handleDeath(Player player) {
		EventGamePlayer loser = getEventPlayer(player);
		loser.setState(EventPlayerState.ELIMINATED);
		onDeath(player);
	}

	public void end() {
		eventManager.handleEventEnd(this);
	}

	public boolean canEnd() {
		int remaining = 0;
		for (EventGamePlayer EventGamePlayer : eventPlayers.values()) {
			if (EventGamePlayer.getState() == EventPlayerState.WAITING || EventGamePlayer.getState() == EventPlayerState.WINNER) {
				remaining++;
			}
		}
		return remaining == 1;
	}

	public Player getWinner() {
		for (EventGamePlayer EventGamePlayer : eventPlayers.values()) {
			if (EventGamePlayer.getState() != EventPlayerState.ELIMINATED) {
				return EventGamePlayer.getPlayer();
			}
		}
		return null;
	}

	public void announce() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			FancyMessage message = new FancyMessage(CC.t("&b" + getHost().getUsername() + " &fis currently hosting a &dSumo Event&f! "));
			message.then("[Click Here]").color(ChatColor.GREEN).command("/sumo join").tooltip(ChatColor.GREEN + "Click to join!").then(" (" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")").color(ChatColor.WHITE);
			message.send(player);
		}
	}

	public void broadcastMessage(String message) {
		eventManager.broadcastEventMessage(this, EVENT_PREFIX + CC.t(message));
	}

	public void onRound() {
		setState(EventState.ROUND_STARTING);
		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();
			if (player != null) {
				//player.teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
				Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
				if (profile.getActiveEvent() != null) {
					EventUtil.resetPlayer(player);
				}
			}
			roundPlayerA = null;
		}

		if (roundPlayerB != null) {
			Player player = roundPlayerB.getPlayer();
			if (player != null) {
				//player.teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
				Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
				if (profile.getActiveEvent() != null) {
					EventUtil.resetPlayer(player);
				}
			}
			roundPlayerB = null;
		}

		roundPlayerA = findRoundPlayer();
		roundPlayerB = findRoundPlayer();

		Player playerA = roundPlayerA.getPlayer();
		Player playerB = roundPlayerB.getPlayer();

		Location spawnA = SoupPvP.getInstance().getSumoHandler().getSpawnA();
		Location spawnB = SoupPvP.getInstance().getSumoHandler().getSpawnB();

		playerA.teleport(spawnA);
		playerB.teleport(spawnB);

		playerA.getInventory().clear();
		playerB.getInventory().clear();

		setEventTask(new SumoTask(this));
	}

	public void onDeath(Player player) {
		EventGamePlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(EventPlayerState.WINNER);
		winner.incrementRoundWins();
		winner.getPlayer().teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
		broadcastMessage(CC.t("&b" + winner.getUsername()  + "&7 eliminated &b" + player.getName() + "&7!"));
		setState(EventState.ROUND_ENDING);
		setEventTask(new kami.gg.souppvp.events.util.task.EventEndTask(this, EventType.SUMO) {
			@Override
			protected void onRound() {
				Sumo.this.onRound();
			}
		});
	}

	public String getRoundDuration() {
		if (state == EventState.ROUND_STARTING) {
			return "00:00";
		} else if (state == EventState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public boolean isFighting(UUID uuid) {
		return (roundPlayerA != null && roundPlayerA.getUuid().equals(uuid)) || (roundPlayerB != null && roundPlayerB.getUuid().equals(uuid));
	}

	private EventGamePlayer findRoundPlayer() {
		EventGamePlayer EventGamePlayer = null;
		for (EventGamePlayer check : getEventPlayers().values()) {
			if (!isFighting(check.getUuid()) && check.getState() == EventPlayerState.WAITING) {
				if (EventGamePlayer == null) {
					EventGamePlayer = check;
					continue;
				}
				if (check.getRoundWins() == 0) {
					EventGamePlayer = check;
					continue;
				}
				if (check.getRoundWins() <= EventGamePlayer.getRoundWins()) {
					EventGamePlayer = check;
				}
			}
		}

		if (EventGamePlayer == null) {
			System.out.println("&cCould not find a new round player");
		}
		return EventGamePlayer;
	}

	public void addSpectator(Player player) {
        eventPlayers.remove(player.getUniqueId());
		spectators.add(player.getUniqueId());
		Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
		profile.setProfileState(ProfileState.SPECTATING_EVENT);
		EventUtil.resetPlayer(player);
		player.teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
		Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
		profile.setProfileState(ProfileState.SPAWN);
		PlayerUtil.resetPlayer(player);
	}

}
