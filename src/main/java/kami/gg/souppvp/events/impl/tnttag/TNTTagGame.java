package kami.gg.souppvp.events.impl.tnttag;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.tnttag.player.TNTGamePlayer;
import kami.gg.souppvp.events.impl.tnttag.player.TNTGamePlayerState;
import kami.gg.souppvp.events.impl.tnttag.task.*;
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
public class TNTTagGame {

    private final Map<UUID, TNTGamePlayer> eventPlayers = new LinkedHashMap<>();
    private List<UUID> spectators = new ArrayList<>();

    private int maxPlayers;
    private int totalPlayers;
    private long roundStartTime;
    private int roundDuration;

    private UUID tntHolder;
    private PlayerSnapshot host;
    private TNTTagState state = TNTTagState.WAITING;
    private transient TNTTagTask eventTask;
    private Cooldown cooldown;

    public TNTTagGame(Player player) {
        this.host = new PlayerSnapshot(player.getUniqueId(), player.getName());
        this.maxPlayers = 100;
        this.roundDuration = 30;
    }

    public void setEventTask(TNTTagTask task) {
        if (eventTask != null) {
            eventTask.cancel();
        }

        eventTask = task;

        if (eventTask != null) {
            eventTask.runTaskTimer(SoupPvP.getInstance(), 0L, 20L);
        }
    }

    public TNTGamePlayer getEventPlayer(Player player) {
        return eventPlayers.get(player.getUniqueId());
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (TNTGamePlayer gp : eventPlayers.values()) {
            Player p = gp.getPlayer();
            if (p != null) players.add(p);
        }
        return players;
    }

    public List<Player> getRemainingPlayers() {
        List<Player> players = new ArrayList<>();
        for (TNTGamePlayer gp : eventPlayers.values()) {
            if (gp.getState() == TNTGamePlayerState.WAITING) {
                Player p = gp.getPlayer();
                if (p != null) players.add(p);
            }
        }
        return players;
    }

    public void handleJoin(Player player) {
        totalPlayers++;
        eventPlayers.put(player.getUniqueId(), new TNTGamePlayer(player));
        broadcastMessage("&b" + player.getName() + " &7has joined the &4TNTTag &7Event! &f(" + getRemainingPlayers().size() + "/" + maxPlayers + ")");

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setTntTagGame(this);
        profile.setProfileState(ProfileState.IN_EVENT);

        EventUtil.resetPlayer(player);
        player.teleport(SoupPvP.getInstance().getTntTagHandler().getSpectatorSpawn().add(0.5, 0, 0.5));
    }

    public void handleLeave(Player player) {
        TNTGamePlayer gp = eventPlayers.remove(player.getUniqueId());

        if (gp != null) {
            totalPlayers--;

            if (player.getUniqueId().equals(tntHolder)) {
                pickNewTNT();
            }
        }

        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setTntTagGame(null);
        profile.setProfileState(ProfileState.SPAWN);
        PlayerUtil.resetPlayer(player);
    }

    public void pickNewTNT() {
        clearTNT();

        List<TNTGamePlayer> alive = new ArrayList<>();

        for (TNTGamePlayer gp : eventPlayers.values()) {
            if (gp.getState() == TNTGamePlayerState.WAITING) {
                alive.add(gp);
            }
        }

        if (alive.isEmpty()) {
            tntHolder = null;
            return;
        }

        TNTGamePlayer chosen = alive.get(new Random().nextInt(alive.size()));
        tntHolder = chosen.getUuid();

        // Teleportar a todos al spawn
        for (TNTGamePlayer player : eventPlayers.values()) {
            Player p = Bukkit.getPlayer(player.getUuid());
            if (p != null) {
                p.teleport(SoupPvP.getInstance().getTntTagHandler().getSpectatorSpawn());
            }
        }

        Player player = chosen.getPlayer();

        if (player != null) {
            applyTNT(player);
        }

        // Resetear el tiempo del round
        roundStartTime = System.currentTimeMillis();
    }

    public void clearTNT() {
        for (TNTGamePlayer gp : eventPlayers.values()) {
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
        TNTGamePlayer gp = getEventPlayer(player);
        if (gp == null) return;

        gp.setState(TNTGamePlayerState.ELIMINATED);
        removeTNT(player);

        addSpectator(player);
        broadcastMessage("&4" + player.getName() + " &7has exploded!");

        if (getRemainingPlayers().size() <= 1) {
            setEventTask(new TNTTagEndStask(this));
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
        for (TNTGamePlayer gp : eventPlayers.values()) {
            if (gp.getState() != TNTGamePlayerState.ELIMINATED) {
                return gp.getPlayer();
            }
        }
        return null;
    }

    public void broadcastMessage(String message) {
        for (TNTGamePlayer player : eventPlayers.values()) {
            Player p = Bukkit.getPlayer(player.getUuid());
            if (p != null) {
                p.sendMessage(CC.translate(message));
            }
        }
    }

    public void sendMessage(Player player, String s) {
        player.sendMessage(CC.translate(s));
    }

    public void announce() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            FancyMessage message = new FancyMessage(CC.translate("&b" + getHost().getUsername() + " &7is currently hosting a &4TNTTag Event&f! "));
            message.then("[Click Here]").color(ChatColor.GREEN).command("/tnttag join").tooltip(ChatColor.GREEN + "Click to join!").then(" (" + getRemainingPlayers().size() + "/" + getMaxPlayers() + ")").color(ChatColor.WHITE);
            message.send(player);
        }
    }

    public void explode() {
        Player player = Bukkit.getPlayer(tntHolder);
        if (player != null) {
            player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
            player.getWorld().spigot().playEffect(player.getLocation(), Effect.EXPLOSION_LARGE);

            TNTGamePlayer gp = getEventPlayer(player);
            if (gp != null) {
                gp.setState(TNTGamePlayerState.ELIMINATED);
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

    protected List<Player> getSpectatorsList() {
        return PlayerUtil.convertUUIDListToPlayerList(spectators);
    }

    public void addSpectator(Player player) {
        if (spectators.contains(player.getUniqueId())) return;

        eventPlayers.remove(player.getUniqueId());
        spectators.add(player.getUniqueId());
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setTntTagGame(this);
        profile.setProfileState(ProfileState.SPECTATING_EVENT);
        EventUtil.resetPlayer(player);
        player.teleport(SoupPvP.getInstance().getTntTagHandler().getSpectatorSpawn());
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        profile.setTntTagGame(null);
        profile.setProfileState(ProfileState.SPAWN);
        PlayerUtil.resetPlayer(player);
    }

    public int getTimeRemaining() {
        if (state != TNTTagState.RUNNING) {
            return roundDuration;
        }

        long elapsed = (System.currentTimeMillis() - roundStartTime) / 1000;
        int remaining = roundDuration - (int) elapsed;
        return Math.max(0, remaining);
    }

    public String getRoundDuration() {
        if (getState() == TNTTagState.STARTING) {
            return "00:" + String.format("%02d", roundDuration);
        } else if (getState() == TNTTagState.RUNNING) {
            int remaining = getTimeRemaining();
            return "00:" + String.format("%02d", remaining);
        } else {
            return "Ending";
        }
    }

    public void onRound() {
        setState(TNTTagState.STARTING);

        for (TNTGamePlayer player : eventPlayers.values()) {
            Player alive = Bukkit.getPlayer(player.getUuid());
            if (alive != null) {
                EventUtil.resetPlayer(alive);
            }
        }

        setEventTask(new TNTTagMatchStartTask(this));
    }

    public void end() {
        SoupPvP.getInstance().getTntTagHandler().setActiveGame(null);
        setEventTask(null);
        Player winner = this.getWinner();

        if (winner == null) {
            Bukkit.broadcastMessage(CC.translate("&cThe TNTTag Event has been cancelled."));

            for (TNTGamePlayer tntGamePlayer : eventPlayers.values()) {
                Player player = tntGamePlayer.getPlayer();
                if (player != null) {
                    Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
                    profile.setProfileState(ProfileState.SPAWN);
                    profile.setTntTagGame(null);
                    PlayerUtil.resetPlayer(player);
                }
            }
            getSpectatorsList().forEach(this::removeSpectator);
        } else {
            Profile winnerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(winner.getUniqueId());
            winnerProfile.setEventsWon(winnerProfile.getEventsWon() + 1);
            winnerProfile.setCredits(winnerProfile.getCredits() + 100);

            Bukkit.broadcastMessage(CC.translate("&b" + winner.getName() + " &fhas won the &4TNTTag &fEvent!"));
            playVictoryEffects(winner);

            Bukkit.getScheduler().runTaskLater(SoupPvP.getInstance(), () -> {
                for (TNTGamePlayer tntGamePlayer : eventPlayers.values()) {
                    Player player = tntGamePlayer.getPlayer();
                    if (player != null) {
                        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
                        profile.setProfileState(ProfileState.SPAWN);
                        profile.setTntTagGame(null);
                        PlayerUtil.resetPlayer(player);
                    }
                }
                getSpectatorsList().forEach(this::removeSpectator);
            }, 60L);
        }
    }

    private void playVictoryEffects(Player winner) {
        Location loc = winner.getLocation();

        winner.playSound(loc, Sound.LEVEL_UP, 1.0F, 1.0F);
        winner.playSound(loc, Sound.FIREWORK_BLAST, 1.0F, 1.0F);

        winner.playEffect(loc.clone().add(0, 1, 0), Effect.FIREWORKS_SPARK, 1);
        winner.playEffect(loc.clone().add(0, 2, 0), Effect.HEART, 1);

        winner.sendTitle(CC.translate("&b&lVICTORY!"), CC.translate("&b+100 Credits"));

        sendMessage(winner, "");
        sendMessage(winner, "&b&lCONGRATULATIONS!");
        sendMessage(winner, "&fYou won the &4TNTTag Event&f!");
        sendMessage(winner, "&fReward: &b+100 Credits");
        sendMessage(winner, "");
    }
}