package kami.gg.souppvp.feats.scoreboard;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.sumo.Sumo;
import kami.gg.souppvp.events.impl.sumo.SumoState;
import kami.gg.souppvp.events.impl.tnttag.TNTTagGame;
import kami.gg.souppvp.events.impl.tnttag.TNTTagState;
import kami.gg.souppvp.feats.staff.StaffManager;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.tier.TierCategory;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.TimeUtil;
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

    private final List<String> footerLines;
    private final List<String> started_sumoLines;
    private final List<String> waiting_sumoLines;
    private final List<String> spawnLines;
    private final List<String> statsLines;
    private final List<String> loadingLines;
    private final List<String> noModMode;
    private final List<String> modMode;
    private final List<String> waiting_tnttagLines;
    private final List<String> running_tnttagLines;
    private final List<String> done_sumoLines;
    private final List<String> done_tnttagLines;

    private final String line;
    private final String dateLine;

    private final boolean linesEnabled;
    private final boolean footerEnabled;
    private final boolean lastLineEnabled;
    private final boolean showDateBelowTitle;
    private final boolean waiting_sumoEnabled;
    private final boolean spawnEnabled;
    private final boolean staffEnabled;
    private final boolean tnttagEnabled;
    private final boolean done_sumoEnabled;
    private final boolean done_tnttagEnabled;

    public ScoreboardAdapter() {
        this.plugin = SoupPvP.getInstance();
        this.scoreboardManager = plugin.getScoreboardManager();
        this.staffManager = plugin.getStaffManager();

        this.footerLines = getStringList("FOOTER.LINES");
        this.started_sumoLines = getStringList("SUMO_EVENT.STARTED_EVENT.LINES");
        this.waiting_sumoLines = getStringList("SUMO_EVENT.WAITING_EVENT.LINES");
        this.spawnLines = getStringList("SPAWN.FORMAT");
        this.statsLines = getStringList("STATS.FORMAT");
        this.loadingLines = getStringList("LOADING.FORMAT");
        this.noModMode = getStringList("STAFF_MODE.VANISH_NO_MODMODE");
        this.modMode = getStringList("STAFF_MODE.MOD_MODE");
        this.waiting_tnttagLines = getStringList("TNTTAG_EVENT.WAITING_EVENT.LINES");
        this.running_tnttagLines = getStringList("TNTTAG_EVENT.RUNNING_EVENT.LINES");
        this.done_sumoLines = getStringList("SUMO_EVENT.DONE_EVENT.LINES");
        this.done_tnttagLines = getStringList("TNTTAG_EVENT.DONE_EVENT.LINES");

        this.line = getString("SCOREBOARD_INFO.LINES");
        this.dateLine = getString("SCOREBOARD_INFO.DATE_LINE");

        this.linesEnabled = getBoolean("SCOREBOARD_INFO.LINES_ENABLED");
        this.staffEnabled = getBoolean("STAFF_MODE.ENABLED");
        this.footerEnabled = getBoolean("FOOTER.ENABLED");
        this.lastLineEnabled = getBoolean("SCOREBOARD_INFO.LAST_LINE_ENABLED");
        this.showDateBelowTitle = getBoolean("SCOREBOARD_INFO.SHOW_DATE_BELOW_TITLE");
        this.waiting_sumoEnabled = getBoolean("SUMO_EVENT.WAITING_EVENT.ENABLED");
        this.spawnEnabled = getBoolean("SPAWN.ENABLED");
        this.tnttagEnabled = getBoolean("TNTTAG_EVENT.ENABLED");
        this.done_sumoEnabled = getBoolean("SUMO_EVENT.DONE_EVENT.ENABLED");
        this.done_tnttagEnabled = getBoolean("TNTTAG_EVENT.DONE_EVENT.ENABLED");
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
            Sumo sumo = profile.getSumoEvent();
            TNTTagGame tnt = profile.getTntTagGame();

            if (showDateBelowTitle) lines.add(dateLine.replace("%date%", TimeUtil.formatScoreboardDate(date)));
            if (linesEnabled) lines.add(line);

            if (done_sumoEnabled && sumo != null && sumo.getRemainingPlayers().size() == 1) {
                String winner = sumo.getRemainingPlayers().getFirst().getDisplayName();
                for (String s : done_sumoLines) {
                    lines.add(s.replace("%winner%", winner));
                }
            }

            if (sumo != null) {
                if (sumo.getState() == SumoState.WAITING) {
                    for (String s : waiting_sumoLines) {
                        lines.add(s
                                .replace("%sumo_players%", String.valueOf(sumo.getRemainingPlayers().size()))
                                .replace("%sumo_max%", String.valueOf(sumo.getMaxPlayers()))
                                .replace("%sumo_state%", "Waiting...")
                        );
                    }
                } else {
                    Player a = Bukkit.getPlayer(sumo.getRoundPlayerA().getUsername());
                    Player b = Bukkit.getPlayer(sumo.getRoundPlayerB().getUsername());
                    int aping = (a instanceof CraftPlayer) ? ((CraftPlayer) a).getHandle().ping : 0;
                    int bping = (b instanceof CraftPlayer) ? ((CraftPlayer) b).getHandle().ping : 0;

                    for (String s : started_sumoLines) {
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
                }
            }

            if (done_tnttagEnabled && tnt != null && tnt.getTotalPlayers() == 1) {
                String winner = tnt.getRemainingPlayers().getFirst().getDisplayName();

                for (String s : done_tnttagLines) {
                    lines.add(s.replace("%winner%", winner));
                }

            }

            if (tnt != null && tnttagEnabled) {
                if (tnt.getState() == TNTTagState.WAITING) {
                    for (String s : waiting_tnttagLines) {
                        lines.add(s
                                .replace("%tnt_players%", String.valueOf(tnt.getEventPlayers().size()))
                                .replace("%tnt_max%", String.valueOf(tnt.getMaxPlayers()))

                        );
                    }
                } else {
                    Player holder = Bukkit.getPlayer(tnt.getTntHolder());

                    for (String s : running_tnttagLines) {
                        lines.add(s
                                .replace("%tnt_players%", String.valueOf(tnt.getEventPlayers().size()))
                                .replace("%tnt_holder%", holder != null ? holder.getName() : "None")
                                .replace("%tnt_time%", String.valueOf(tnt.getRoundDuration()))
                                .replace("%tnt_total%", String.valueOf(tnt.getTotalPlayers()))
                        );
                    }
                }

            }

            if (footerEnabled) lines.addAll(footerLines);
            if (linesEnabled && lastLineEnabled) lines.add(line);

            return CC.t(lines);
        }

        if (staffEnabled && staff) {
            if (showDateBelowTitle) {
                lines.add(dateLine.replace("%date%", TimeUtil.formatScoreboardDate(date)));
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
                    .replace("%credits%", String.valueOf(profile.getCredits()))
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

        if (waiting_sumoEnabled && profile.getSumoEvent() == null && profile.getProfileState() == ProfileState.SPAWN) {
            Sumo active = plugin.getSumoHandler().getActiveSumo();

            if (active != null && active.getState() == SumoState.WAITING) {
                for (String s : waiting_sumoLines) {
                    lines.add(s
                            .replace("%sumo_players%", String.valueOf(active.getEventPlayers().size()))
                            .replace("%sumo_max%", String.valueOf(active.getMaxPlayers()))
                            .replace("%sumo_state%", "Waiting...")
                    );
                }
            }
        }

        if (tnttagEnabled && profile.getTntTagGame() == null && profile.getProfileState() == ProfileState.SPAWN) {
            TNTTagGame game = plugin.getTntTagHandler().getActiveGame();

            if (game != null && game.getState() == TNTTagState.WAITING) {
                for (String s : waiting_tnttagLines) {
                    lines.add(s
                            .replace("%tnt_players%", String.valueOf(game.getEventPlayers().size()))
                            .replace("%tnt_max%", String.valueOf(game.getMaxPlayers()))
                    );
                }
            }
        }

        if (staffEnabled && vanish) {
            for (String s : noModMode) {
                lines.add(s.replace("%vanished%", "&a✔"));
            }
        }

        if (scoreboardManager.getScoreboardConfig().getBoolean("TIMERS.ENABLED") && profile.getSumoEvent() == null) {
            // Spawn Timer
            long tp = plugin.getSpawnTeleportationHandler().getSpawnTeleporataion().getOrDefault(player.getUniqueId(), 0L);

            if (profile.isTeleportingToSpawn() && tp > System.currentTimeMillis()) {
                String format = scoreboardManager.getScoreboardConfig().getString("TIMERS.SPAWN");
                if (format != null) {
                    String line = format.replace("%time%", TimeUtil.convertToHhMmSs((tp - System.currentTimeMillis()) / 1000));
                    lines.add(CC.t(line));
                    lines.add("");
                }
            }

            // Combat Timer
            Long combatTime = plugin.getCombatTagsHandler().getCombatTags().get(player.getUniqueId());
            if (combatTime != null && combatTime > System.currentTimeMillis()) {
                String format = scoreboardManager.getScoreboardConfig().getString("TIMERS.COMBAT");
                if (format != null) {
                    String line = format.replace("%time%", TimeUtil.convertToHhMmSs((combatTime - System.currentTimeMillis()) / 1000));
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
                clone.add(dateLine.replace("%date%", TimeUtil.formatScoreboardDate(date)));
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
}