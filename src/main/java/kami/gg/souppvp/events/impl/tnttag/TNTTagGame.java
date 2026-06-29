package kami.gg.souppvp.events.impl.tnttag;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventManager;
import kami.gg.souppvp.events.util.EventGamePlayer;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.events.EventType;
import kami.gg.souppvp.events.util.EventPlayerState;
import kami.gg.souppvp.events.impl.tnttag.task.TNTTagGameTask;
import kami.gg.souppvp.events.util.task.EventEndTask;
import kami.gg.souppvp.events.util.task.EventTask;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.*;
import kami.gg.souppvp.util.fanciful.FancyMessage;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter @Setter
public class TNTTagGame implements Event {

    private final Map<UUID, EventGamePlayer> eventPlayers = new LinkedHashMap<>();
    private List<UUID> spectators = new ArrayList<>();

    private int maxPlayers;
    private int totalPlayers;
    private long roundStartTime;
    private int roundDuration;

    private UUID tntHolder;
    private PlayerSnapshot host;
    private EventState state = EventState.WAITING;
    private transient EventTask eventTask;
    private Cooldown cooldown;
    private EventManager eventManager;

    public TNTTagGame(Player player) {
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.eventManager = SoupPvP.getInstance().getEventManager();
        this.maxPlayers = 100;
        this.roundDuration = 30;
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

    public EventGamePlayer getEventPlayer(Player player) {
        return eventPlayers.get(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (EventGamePlayer gp : eventPlayers.values()) {
            Player p = gp.getPlayer();
            if (p != null) players.add(p);
        }
        return players;
    }

    public List<Player> getRemainingPlayers() {
        List<Player> players = new ArrayList<>();
        for (EventGamePlayer gp : eventPlayers.values()) {
            if (gp.getState() == EventPlayerState.WAITING) {
                Player p = gp.getPlayer();
                if (p != null) players.add(p);
            }
        }
        return players;
    }

    public void handleJoin(Player player) {
        totalPlayers++;
        eventPlayers.put(player.getUniqueId(), new EventGamePlayer(player));

        eventManager.handleEventJoin(player, SoupPvP.getInstance().getTntTagHandler().getSpectatorSpawn().add(0.5, 0, 0.5));
        broadcastMessage("&b" + player.getName() + " &7has joined the &4TNTTag &7Event! &f(" + getRemainingPlayers().size() + "/" + maxPlayers + ")");
    }

    public void handleLeave(Player player) {
        EventGamePlayer gp = eventPlayers.remove(player.getUniqueId());

        if (gp != null) {
            totalPlayers--;

            if (player.getUniqueId().equals(tntHolder)) {
                pickNewTNT();
            }
        }

        eventManager.handleEventLeave(player);
    }

    @Override
    public boolean hasRounds() {
        return true;
    }

    public void pickNewTNT() {
        clearTNT();

        List<EventGamePlayer> alive = new ArrayList<>();

        for (EventGamePlayer gp : eventPlayers.values()) {
            if (gp.getState() == EventPlayerState.WAITING) {
                alive.add(gp);
            }
        }

        if (alive.isEmpty()) {
            tntHolder = null;
            return;
        }

        EventGamePlayer chosen = alive.get(new Random().nextInt(alive.size()));
        tntHolder = chosen.getUuid();

        for (EventGamePlayer player : eventPlayers.values()) {
            Player p = Bukkit.getPlayer(player.getUuid());
            if (p != null) {
                p.teleport(SoupPvP.getInstance().getTntTagHandler().getSpectatorSpawn());
            }
        }

        Player player = chosen.getPlayer();

        if (player != null) {
            applyTNT(player);
        }

        roundStartTime = System.currentTimeMillis();
    }

    public void clearTNT() {
        for (EventGamePlayer gp : eventPlayers.values()) {
            Player player = gp.getPlayer();
            if (player == null) continue;

            ItemStack helmet = player.getInventory().getHelmet();
            if (helmet != null && helmet.getType() == Material.TNT) {
                player.getInventory().setHelmet(null);
            }
        }
    }

    public void applyTNT(Player player) {
        broadcastMessage("&c" + player.getDisplayName() + " &fis IT");
        player.getInventory().setHelmet(new ItemStack(Material.TNT));
    }

    public void removeTNT(Player player) {
        player.getInventory().setHelmet(null);
    }

    public void handleDeath(Player player) {
        EventGamePlayer gp = getEventPlayer(player);
        if (gp == null) return;

        gp.setState(EventPlayerState.ELIMINATED);
        removeTNT(player);

        addSpectator(player);
        broadcastMessage("&4" + player.getName() + " &7has exploded!");

        if (getRemainingPlayers().size() <= 1) {
            setEventTask(new EventEndTask(this, EventType.TNTTAG) {
                @Override
                protected void onRound() {
                    TNTTagGame.this.pickNewTNT();
                }
            });
            return;
        }

        if (player.getUniqueId().equals(tntHolder)) {
            pickNewTNT();
        }
    }

    public boolean canEnd() {
        return getRemainingPlayers().size() <= 1;
    }

    public Player getWinner() {
        for (EventGamePlayer gp : eventPlayers.values()) {
            if (gp.getState() != EventPlayerState.ELIMINATED) {
                return gp.getPlayer();
            }
        }
        return null;
    }

    public void broadcastMessage(String message) {
        eventManager.broadcastEventMessage(this, CC.t(message));
    }

    public void sendMessage(Player player, String s) {
        player.sendMessage(CC.t(s));
    }

    public void announce() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            FancyMessage message = new FancyMessage(CC.t("&b" + getHost().getUsername() + " &7is currently hosting a &4TNTTag Event&f! "));
            message.then("[Click Here]").color(ChatColor.GREEN).command("/tnttag join").tooltip(ChatColor.GREEN + "Click to join!").then(" (" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")").color(ChatColor.WHITE);
            message.send(player);
        }
    }

    public void explode() {
        Player player = Bukkit.getPlayer(tntHolder);
        if (player != null) {
            player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
            player.getWorld().spigot().playEffect(player.getLocation(), Effect.EXPLOSION_LARGE);

            EventGamePlayer gp = getEventPlayer(player);
            if (gp != null) {
                gp.setState(EventPlayerState.ELIMINATED);
            }

            removeTNT(player);
            handleDeath(player);
        }

        if (getRemainingPlayers().size() <= 1) {
            end();
        } else {
            pickNewTNT();
        }
    }

    public List<Player> getSpectatorsList() {
        return PlayerUtil.convertUUIDListToPlayerList(spectators);
    }

    public void addSpectator(Player player) {
        if (spectators.contains(player.getUniqueId())) return;

        eventPlayers.remove(player.getUniqueId());
        spectators.add(player.getUniqueId());
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setProfileState(ProfileState.SPECTATING_EVENT);
        EventUtil.resetPlayer(player);
        player.teleport(SoupPvP.getInstance().getTntTagHandler().getSpectatorSpawn());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setProfileState(ProfileState.SPAWN);
        PlayerUtil.resetPlayer(player);
    }

    public int getTimeRemaining() {
        if (state != EventState.RUNNING) {
            return roundDuration;
        }

        long elapsed = (System.currentTimeMillis() - roundStartTime) / 1000;
        int remaining = roundDuration - (int) elapsed;
        return Math.max(0, remaining);
    }

    @Override
    public EventState getState() {
        return state;
    }

    @Override
    public String getEventName() {
        return "TNTTag";
    }

    @Override
    public EventType getType() {
        return EventType.TNTTAG;
    }

    @Override
    public boolean hasPlayer(Player player) {
        return eventPlayers.containsKey(player.getUniqueId()) || spectators.contains(player.getUniqueId());
    }

    public String getRoundDuration() {
        if (state == EventState.STARTING) {
            return "00:" + String.format("%02d", roundDuration);
        } else if (state == EventState.RUNNING) {
            int remaining = getTimeRemaining();
            return "00:" + String.format("%02d", remaining);
        } else {
            return "Ending";
        }
    }

    public void onRound() {
        setState(EventState.STARTING);

        for (EventGamePlayer player : eventPlayers.values()) {
            Player alive = Bukkit.getPlayer(player.getUuid());
            if (alive != null) {
                EventUtil.resetPlayer(alive);
            }
        }

        setEventTask(new TNTTagGameTask(this));
    }

    public void end() {
        eventManager.handleEventEnd(this);
    }

    public void playVictoryEffects(Player winner) {
        Location loc = winner.getLocation();

        winner.playSound(loc, Sound.LEVEL_UP, 1.0F, 1.0F);
        winner.playSound(loc, Sound.FIREWORK_BLAST, 1.0F, 1.0F);

        winner.playEffect(loc.clone().add(0, 1, 0), Effect.FIREWORKS_SPARK, 1);
        winner.playEffect(loc.clone().add(0, 2, 0), Effect.HEART, 1);

        winner.sendTitle(CC.t("&b&lVICTORY!"), CC.t("&b+100 Credits"));

        sendMessage(winner, "");
        sendMessage(winner, "&b&lCONGRATULATIONS!");
        sendMessage(winner, "&fYou won the &4TNTTag Event&f!");
        sendMessage(winner, "&fReward: &b+100 Credits");
        sendMessage(winner, "");
    }
}