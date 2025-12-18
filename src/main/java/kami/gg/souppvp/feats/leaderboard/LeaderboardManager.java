package kami.gg.souppvp.feats.leaderboard;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class LeaderboardManager {

    private final Map<LeaderboardType, List<LeaderboardEntry>> cachedLeaderboards;
    private final long updateInterval = 20 * 60 * 2;

    public LeaderboardManager() {
        super();
        this.cachedLeaderboards = new ConcurrentHashMap<>();
    }

    public void updateAllLeaderboards() {
        for (LeaderboardType type : LeaderboardType.values()) {
            updateLeaderboard(type);
        }
    }

    public void updateLeaderboard(LeaderboardType type) {
        List<LeaderboardEntry> entries = new ArrayList<>();

        for (Profile profile : SoupPvP.getInstance().getProfilesHandler().getProfiles().values()) {
            double value = type.getValue(profile);
            if (value > 0) {
                entries.add(new LeaderboardEntry(profile.getUuid(), profile.getUsername(), value));
            }
        }

        // Sort descending
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        cachedLeaderboards.put(type, entries);
    }

    public List<LeaderboardEntry> getTop(LeaderboardType type, int limit) {
        List<LeaderboardEntry> entries = cachedLeaderboards.get(type);
        if (entries == null) {
            updateLeaderboard(type);
            entries = cachedLeaderboards.get(type);
        }

        return entries.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public int getPosition(LeaderboardType type, UUID uuid) {
        List<LeaderboardEntry> entries = cachedLeaderboards.get(type);
        if (entries == null) return -1;

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getUuid().equals(uuid)) {
                return i + 1; // 1-indexed position
            }
        }

        return -1;
    }

    @Getter
    public static class LeaderboardEntry {
        private final UUID uuid;
        private final String name;
        private final double value;

        public LeaderboardEntry(UUID uuid, String name, double value) {
            this.uuid = uuid;
            this.name = name;
            this.value = value;
        }

        public String getFormattedValue(LeaderboardType type) {
            return type.format(value);
        }
    }
}