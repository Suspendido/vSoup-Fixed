package kami.gg.souppvp.feats.scoreboard;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.Event;
import kami.gg.souppvp.events.EventManager;
import kami.gg.souppvp.events.util.EventState;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.feats.staff.StaffManager;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.Formatter;
import kami.gg.souppvp.util.assemble.AssembleAdapter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {

    private final SoupPvP plugin;
    private final StaffManager staffManager;
    private final ScoreboardManager scoreboardManager;
    private final EventManager eventManager;

    private final List<String> footerLines;
    private final List<String> started_sumoLines;
    private final List<String> waiting_eventLines;
    private final List<String> spawnLines;
    private final List<String> statsLines;
    private final List<String> loadingLines;
    private final List<String> noModMode;
    private final List<String> modMode;
    private final List<String> running_tnttagLines;
    private final List<String> done_eventLines;

    private final String line;
    private final String dateLine;

    private final boolean linesEnabled;
    private final boolean footerEnabled;
    private final boolean lastLineEnabled;
    private final boolean showDateBelowTitle;
    private final boolean waiting_eventEnabled;
    private final boolean spawnEnabled;
    private final boolean staffEnabled;
    private final boolean done_eventEnabled;

    public ScoreboardAdapter() {
        this.plugin = SoupPvP.getInstance();
        this.scoreboardManager = plugin.getScoreboardManager();
        this.staffManager = plugin.getStaffManager();
        this.eventManager = new EventManager(plugin);

        this.footerLines = getStringList("FOOTER.LINES");
        this.started_sumoLines = getStringList("SUMO_EVENT.STARTED_EVENT.LINES");
        this.waiting_eventLines = getStringList("EVENT.WAITING.LINES");
        this.spawnLines = getStringList("SPAWN.FORMAT");
        this.statsLines = getStringList("STATS.FORMAT");
        this.loadingLines = getStringList("LOADING.FORMAT");
        this.noModMode = getStringList("STAFF_MODE.VANISH_NO_MODMODE");
        this.modMode = getStringList("STAFF_MODE.MOD_MODE");
        this.running_tnttagLines = getStringList("TNTTAG_EVENT.RUNNING_EVENT.LINES");
        this.done_eventLines = getStringList("EVENT.DONE.LINES");

        this.line = getString("SCOREBOARD_INFO.LINES");
        this.dateLine = getString("SCOREBOARD_INFO.DATE_LINE");

        this.linesEnabled = getBoolean("SCOREBOARD_INFO.LINES_ENABLED");
        this.staffEnabled = getBoolean("STAFF_MODE.ENABLED");
        this.footerEnabled = getBoolean("FOOTER.ENABLED");
        this.lastLineEnabled = getBoolean("SCOREBOARD_INFO.LAST_LINE_ENABLED");
        this.showDateBelowTitle = getBoolean("SCOREBOARD_INFO.SHOW_DATE_BELOW_TITLE");
        this.waiting_eventEnabled = getBoolean("EVENT.WAITING.ENABLED");
        this.spawnEnabled = getBoolean("SPAWN.ENABLED");
        this.done_eventEnabled = getBoolean("EVENT.DONE.ENABLED");
    }

    @Override
    public String getTitle(Player player) {
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile == null || !profile.getEnableScoreboard()) return "";

        return scoreboardManager.getAnimatedTitle(player);
    }

    @Override
    public List<String> getLines(Player player) {
        Profile profile = plugin.getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Date date = (showDateBelowTitle ? new Date() : null);
        boolean isInSpawn = plugin.getSpawnHandler().getCuboid().contains(player) && profile.getProfileState() == ProfileState.SPAWN;
        boolean staff = staffManager.isStaffEnabled(player);
        boolean vanish = staffManager.isVanished(player);
        int footer = footerEnabled ? 2 : 0;
        int numberOfLines = (lastLineEnabled ? 2 : 1) + (showDateBelowTitle ? 1 : 0);

        if (profile == null || !profile.getEnableScoreboard()) {
            return new ArrayList<>();
        }

        if (!profile.getLoaded()) {
            return CC.t(new ArrayList<>(loadingLines));
        }

        List<String> lines = new ArrayList<>();

        if (profile.getProfileState() == ProfileState.IN_EVENT || profile.getProfileState() == ProfileState.SPECTATING_EVENT) {
            Event playerEvent = getPlayerEvent(profile);

            if (showDateBelowTitle) lines.add(dateLine.replace("%date%", Formatter.formatScoreboardDate(date)));
            if (linesEnabled) lines.add(line);

            if (playerEvent != null) {
                addEventScoreboard(lines, playerEvent);
            }

            if (footerEnabled) lines.addAll(footerLines);
            if (linesEnabled && lastLineEnabled) lines.add(line);

            return CC.t(lines);
        }

        if (staffEnabled && staff) {
            if (showDateBelowTitle) {
                lines.add(dateLine.replace("%date%", Formatter.formatScoreboardDate(date)));
            }

            if (linesEnabled) {
                lines.add(line);
            }

            for (String s : modMode) {
                lines.add(s
                        .replace("%vanished%", vanish ? "&a✔" : "&c✖")
                        .replace("%players%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("%hidestaff%", staffManager.isHideStaff(player) ? "&a✔" : "&c✖")
                        .replace("%max_online%", String.valueOf(Bukkit.getMaxPlayers()))
                        .replace("%staff%", String.valueOf(staffManager.getStaffMembers().size()))
                        .replace("%tps%", getTPSColored())
                );
            }

            if (footerEnabled) lines.addAll(footerLines);
            if (linesEnabled && lastLineEnabled) lines.add(line);

            return CC.t(lines);
        }

        for (String s : statsLines) {
            if (s.contains("%bounty%") && profile.getBounty() <= 0) continue;
            if (s.contains("%killstreak%") && profile.getCurrentKillstreak() <= 0) continue;

            lines.add(s
                    .replace("%kills%", String.valueOf(profile.getKills()))
                    .replace("%deaths%", String.valueOf(profile.getDeaths()))
                    .replace("%killstreak%", String.valueOf(profile.getCurrentKillstreak()))
                    .replace("%credits%", Formatter.formatBalance(profile.getCredits()))
                    .replace("%bounty%", String.valueOf(profile.getBounty()))
            );
        }

        if (spawnEnabled && isInSpawn) {
            Kit current = plugin.getKitsHandler().getKitByName(profile.getCurrentKit());
            TierCategory category = TierCategory.getCategoryByName(profile.getSelectedTierIcon());
            for (String s : spawnLines) {
                lines.add(s
                        .replace("%tier%", category.getColor() + profile.getTier() + category.getFormattedIcon())
                        .replace("%kit%", profile.getCurrentKit())
                        .replace("%kit_color%", String.valueOf(current.getRarityType().getColor()))
                );
            }
        }

        if (profile.getProfileState() == ProfileState.SPAWN && getPlayerEvent(profile) == null) {
            for (Event event : eventManager.getWaitingEvents()) {
                addWaitingEventScoreboard(lines, event);
            }
        }

        if (staffEnabled && vanish) {
            for (String s : noModMode) {
                lines.add(s.replace("%vanished%", "&a✔"));
            }
        }

        if (scoreboardManager.getScoreboardConfig().getBoolean("TIMERS.ENABLED") && profile.getActiveEvent() == null) {
            // Combat Timer
            Timer combatTimer = plugin.getTimerManager().getTimer("Combat");
            if (combatTimer != null && combatTimer.hasTimer(player)) {
                long remaining = combatTimer.getRemaining(player);
                String format = scoreboardManager.getScoreboardConfig().getString("TIMERS.COMBAT");
                if (format != null) {
                    String line = format.replace("%time%", Formatter.convertToHhMmSs(remaining / 1000));
                    lines.add(CC.t(line));
                    lines.add("");
                }
            }
        }

        if (footerEnabled) {
            lines.addAll(footerLines);
        }

        if (lines.isEmpty()) {
            return null;
        }

        if (linesEnabled) {
            List<String> clone = new ArrayList<>();

            if (showDateBelowTitle) {
                clone.add(dateLine.replace("%date%", Formatter.formatScoreboardDate(date)));
            }

            if (!lines.getFirst().equals(line)) {
                clone.add(line);
            }

            clone.addAll(lines);

            if (!lines.getLast().equals(line) && lastLineEnabled) {
                clone.add(line);
            }

            lines = clone;

            if (lines.size() == numberOfLines + footer) return null;
        }

        if (!linesEnabled && footerEnabled && lines.size() == 2) {
            return null;
        }

        // Fix all the double lines
        Iterator<String> iterator = lines.iterator();
        String previous = null;

        while (iterator.hasNext()) {
            String next = iterator.next();

            if (previous == null) {
                previous = next;
                continue;
            }

            if (previous.equals(line) && next.equals(line)) {
                previous = null;
                iterator.remove();
                continue;
            }

            previous = next;
        }

        // Fixes the line above footer
        if (lines.size() >= footer + numberOfLines) {
            int index = lines.size() - (footer + (lastLineEnabled ? 1 : 0));
            String doubleLine = lines.get(index - 1);

            if (index != lines.size() && doubleLine.equals(line)) {
                lines.remove(index - 1);
            }
        }

        return CC.t(lines);
    }

    public String getString(String path) {
        String string = scoreboardManager.getScoreboardConfig().getString(path);
        assert string != null;
        return (string.isEmpty() ? null : string);
    }

    public List<String> getStringList(String path) {
        List<String> s = scoreboardManager.getScoreboardConfig().getStringList(path);
        assert s != null;
        return (s.isEmpty() ? null : s);
    }

    public boolean getBoolean(String path) {
        return scoreboardManager.getScoreboardConfig().getBoolean(path);
    }

    public String getTPSColored() {
        double tps = MinecraftServer.getServer().recentTps[0];
        String color = (tps > 18 ? "§a" : tps > 16 ? "§e" : "§c");
        String asterisk = (tps > 20 ? "*" : "");
        return color + asterisk + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

    private Event getPlayerEvent(Profile profile) {
        Player player = Bukkit.getPlayer(profile.getUuid());
        if (player == null) return null;
        return eventManager.getPlayerEvent(player);
    }

    private void addEventScoreboard(List<String> lines, Event event) {
        List<String> runningLines = switch (event.getType()) {
            case SUMO -> started_sumoLines;
            case TNTTAG -> running_tnttagLines;
        };

        boolean isWaiting = event.getState() == EventState.WAITING;

        if (isWaiting) {
            addWaitingEventScoreboard(lines, event);
        } else {
            addRunningEventScoreboard(lines, event, runningLines);
            
            // Win check
            if (done_eventEnabled && eventManager.isEventInWinState(event)) {
                Player winner = eventManager.getEventWinnerIfFinished(event);
                if (winner != null) {
                    for (String s : done_eventLines) {
                        lines.add(s.replace("%winner%", winner.getDisplayName()).replace("%event_name%", event.getEventName()));
                    }
                }
            }
        }
    }

    private void addWaitingEventScoreboard(List<String> lines, Event event) {
        if (!waiting_eventEnabled) return;
        
        for (String s : waiting_eventLines) {
            String line = s
                    .replace("%event_name%", event.getEventName())
                    .replace("%players%", String.valueOf(event.getRemainingPlayers().size()))
                    .replace("%max_players%", String.valueOf(event.getMaxPlayers()))
                    .replace("%state%", "Waiting...");
            
            lines.add(line);
        }
    }

    private void addRunningEventScoreboard(List<String> lines, Event event, List<String> runningLines) {
        if (runningLines == null) return;
        
        switch (event.getType()) {
            case SUMO:
                Sumo sumo = (Sumo) event;
                Player a = Bukkit.getPlayer(sumo.getRoundPlayerA().getUsername());
                Player b = Bukkit.getPlayer(sumo.getRoundPlayerB().getUsername());
                int aping = (a instanceof CraftPlayer) ? ((CraftPlayer) a).getHandle().ping : 0;
                int bping = (b instanceof CraftPlayer) ? ((CraftPlayer) b).getHandle().ping : 0;

                for (String s : runningLines) {
                    lines.add(s
                            .replace("%sumo_remaining%", String.valueOf(sumo.getRemainingPlayers().size()))
                            .replace("%sumo_total%", String.valueOf(sumo.getTotalPlayers()))
                            .replace("%sumo_duration%", String.valueOf(sumo.getRoundDuration()))
                            .replace("%sumo_player_a%", sumo.getRoundPlayerA().getUsername())
                            .replace("%sumo_player_b%", sumo.getRoundPlayerB().getUsername())
                            .replace("%sumo_ping_a%", String.valueOf(aping))
                            .replace("%sumo_ping_b%", String.valueOf(bping))
                    );
                }
                break;
            case TNTTAG:
                TNTTagGame tnt = (TNTTagGame) event;
                Player holder = Bukkit.getPlayer(tnt.getTntHolder());

                for (String s : runningLines) {
                    lines.add(s
                            .replace("%tnt_players%", String.valueOf(tnt.getEventPlayers().size()))
                            .replace("%tnt_holder%", holder != null ? holder.getName() : "None")
                            .replace("%tnt_time%", String.valueOf(tnt.getRoundDuration()))
                            .replace("%tnt_total%", String.valueOf(tnt.getTotalPlayers()))
                    );
                }
                break;
        }
    }
}