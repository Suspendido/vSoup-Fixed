package kami.gg.souppvp.events.impl.sumo;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.sumo.player.SumoPlayer;
import kami.gg.souppvp.events.impl.sumo.player.SumoPlayerState;
import kami.gg.souppvp.events.impl.sumo.task.SumoRoundEndTask;
import kami.gg.souppvp.events.impl.sumo.task.SumoRoundStartTask;
import kami.gg.souppvp.events.impl.sumo.task.SumoTask;
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
public class Sumo {

	public static String EVENT_PREFIX = CC.translate("");

	private String name;
	private SumoState state = SumoState.WAITING;
	private SumoTask eventTask;
	private PlayerSnapshot host;
	private LinkedHashMap<UUID, SumoPlayer> eventPlayers = new LinkedHashMap<>();
	private List<UUID> spectators = new ArrayList<>();
    private Cooldown cooldown;
    private SumoPlayer roundPlayerA;
    private SumoPlayer roundPlayerB;

    private int maxPlayers;
    private int totalPlayers;
    private long roundStart;

	public Sumo(Player player) {
		this.name = player.getName();
		this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
		this.maxPlayers = 100;
	}

	public void setEventTask(SumoTask task) {
		if (eventTask != null) {
			eventTask.cancel();
		}

		eventTask = task;

		if (eventTask != null) {
			eventTask.runTaskTimer(SoupPvP.getInstance(), 0L, 20L);
		}
	}

	public boolean isWaiting() {
		return state == SumoState.WAITING;
	}

	public boolean isFighting() {
		return state == SumoState.ROUND_FIGHTING;
	}

	public SumoPlayer getEventPlayer(Player player) {
		return eventPlayers.get(player.getUniqueId());
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			Player player = sumoPlayer.getPlayer();

			if (player != null) {
				players.add(player);
			}
		}

		return players;
	}

	public List<Player> getRemainingPlayers() {
		List<Player> players = new ArrayList<>();

		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			if (sumoPlayer.getState() == SumoPlayerState.WAITING) {
				Player player = sumoPlayer.getPlayer();
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public void handleJoin(Player player) {
		eventPlayers.put(player.getUniqueId(), new SumoPlayer(player));
		broadcastMessage(CC.translate("&b" + player.getName() + " &7has joined the &bSumo &7Event! &f(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")"));
		Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
		profile.setSumoEvent(this);
		profile.setProfileState(ProfileState.IN_EVENT);
		EventUtil.resetPlayer(player);

		player.teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn().add(0.5, 0, 0.5));
	}

	public void handleLeave(Player player) {
		if (isFighting(player.getUniqueId())) {
			handleDeath(player);
		}
		eventPlayers.remove(player.getUniqueId());
		if (state == SumoState.WAITING) {
			broadcastMessage(CC.translate("&b" + player.getName() + " &7has left the &bSumo &7Event! &f(" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")"));
		}
		Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
		profile.setProfileState(ProfileState.SPAWN);
		profile.setSumoEvent(null);
		PlayerUtil.resetPlayer(player);
	}

	protected List<Player> getSpectatorsList() {
		return PlayerUtil.convertUUIDListToPlayerList(spectators);
	}

	public void handleDeath(Player player) {
		SumoPlayer loser = getEventPlayer(player);
		loser.setState(SumoPlayerState.ELIMINATED);
		onDeath(player);
	}

	public void end() {
		SoupPvP.getInstance().getSumoHandler().setActiveSumo(null);
		setEventTask(null);
		Player winner = this.getWinner();
		if (winner == null) {
			Bukkit.broadcastMessage(CC.translate("&cThe Sumo Event has been cancelled."));
		} else {
			Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(winner.getUniqueId());
			profile.setEventsWon(profile.getEventsWon() + 1);
			profile.setCredits(profile.getCredits() + 100);
			Bukkit.broadcastMessage(CC.translate("&b" + winner.getName() + " &7has won the &bSumo &7Event!"));
		}
		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			Player player = sumoPlayer.getPlayer();

			if (player != null) {
				Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
				profile.setProfileState(ProfileState.SPAWN);
				profile.setSumoEvent(null);
				PlayerUtil.resetPlayer(player);
			}
		}
		getSpectatorsList().forEach(this::removeSpectator);
	}

	public boolean canEnd() {
		int remaining = 0;
		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			if (sumoPlayer.getState() == SumoPlayerState.WAITING) {
				remaining++;
			}
		}
		return remaining == 1;
	}

	public Player getWinner() {
		for (SumoPlayer sumoPlayer : eventPlayers.values()) {
			if (sumoPlayer.getState() != SumoPlayerState.ELIMINATED) {
				return sumoPlayer.getPlayer();
			}
		}
		return null;
	}

	public void announce() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			FancyMessage message = new FancyMessage(CC.translate("&b" + getHost().getUsername() + " &fis currently hosting a &dSumo Event&f! "));
			message.then("[Click Here]").color(ChatColor.GREEN).command("/sumo join").tooltip(ChatColor.GREEN + "Click to join!").then(" (" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")").color(ChatColor.WHITE);
			message.send(player);
		}
	}

	public void broadcastMessage(String message) {
		for (Player player : getPlayers()) {
			player.sendMessage(EVENT_PREFIX + CC.translate(message));
		}
	}

	public void onRound() {
		setState(SumoState.ROUND_STARTING);
		if (roundPlayerA != null) {
			Player player = roundPlayerA.getPlayer();
			if (player != null) {
				//player.teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
				Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
				if (profile.getSumoEvent() != null) {
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
				if (profile.getSumoEvent() != null) {
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

		setEventTask(new SumoRoundStartTask(this));
	}

	public void onDeath(Player player) {
		SumoPlayer winner = roundPlayerA.getUuid().equals(player.getUniqueId()) ? roundPlayerB : roundPlayerA;
		winner.setState(SumoPlayerState.WAITING);
		winner.incrementRoundWins();
		winner.getPlayer().teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
		broadcastMessage(CC.translate("&b" + winner.getUsername()  + "&7 eliminated &b" + player.getName() + "&7!"));
		setState(SumoState.ROUND_ENDING);
		setEventTask(new SumoRoundEndTask(this));
	}

	public String getRoundDuration() {
		if (getState() == SumoState.ROUND_STARTING) {
			return "00:00";
		} else if (getState() == SumoState.ROUND_FIGHTING) {
			return TimeUtil.millisToTimer(System.currentTimeMillis() - roundStart);
		} else {
			return "Ending";
		}
	}

	public boolean isFighting(UUID uuid) {
		return (roundPlayerA != null && roundPlayerA.getUuid().equals(uuid)) || (roundPlayerB != null && roundPlayerB.getUuid().equals(uuid));
	}

	private SumoPlayer findRoundPlayer() {
		SumoPlayer sumoPlayer = null;
		for (SumoPlayer check : getEventPlayers().values()) {
			if (!isFighting(check.getUuid()) && check.getState() == SumoPlayerState.WAITING) {
				if (sumoPlayer == null) {
					sumoPlayer = check;
					continue;
				}
				if (check.getRoundWins() == 0) {
					sumoPlayer = check;
					continue;
				}
				if (check.getRoundWins() <= sumoPlayer.getRoundWins()) {
					sumoPlayer = check;
				}
			}
		}

		if (sumoPlayer == null) {
			System.out.println("&cCould not find a new round player");
		}
		return sumoPlayer;
	}

	public void addSpectator(Player player) {
        eventPlayers.remove(player.getUniqueId());
		spectators.add(player.getUniqueId());
		Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
		profile.setSumoEvent(this);
		profile.setProfileState(ProfileState.SPECTATING_EVENT);
		EventUtil.resetPlayer(player);
		player.teleport(SoupPvP.getInstance().getSumoHandler().getSpectatorSpawn());
	}

	public void removeSpectator(Player player) {
		spectators.remove(player.getUniqueId());
		Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
		profile.setSumoEvent(null);
		profile.setProfileState(ProfileState.SPAWN);
		PlayerUtil.resetPlayer(player);
	}

}
