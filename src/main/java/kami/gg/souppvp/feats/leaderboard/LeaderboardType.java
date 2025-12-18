package kami.gg.souppvp.feats.leaderboard;

import kami.gg.souppvp.profile.Profile;
import lombok.Getter;

import java.text.DecimalFormat;
import java.util.function.Function;

@Getter
public enum LeaderboardType {

    KILLS("Kills", profile -> (double) profile.getKills(), value -> String.valueOf(value.intValue())),
    DEATHS("Deaths", profile -> (double) profile.getDeaths(), value -> String.valueOf(value.intValue())),
    CREDITS("Credits", profile -> (double) profile.getCredits(), value -> String.valueOf(value.intValue())),
    KDR("K/D Ratio", profile -> {
        int kills = profile.getKills();
        int deaths = profile.getDeaths();
        return deaths == 0 ? (double) kills : (double) kills / deaths;
    }, value -> new DecimalFormat("0.00").format(value)),
    KILLSTREAK("Killstreak", profile -> (double) profile.getCurrentKillstreak(), value -> String.valueOf(value.intValue()));

    private final String displayName;
    private final Function<Profile, Number> valueGetter;
    private final Function<Double, String> formatter;

    LeaderboardType(String displayName, Function<Profile, Number> valueGetter, Function<Double, String> formatter) {
        this.displayName = displayName;
        this.valueGetter = valueGetter;
        this.formatter = formatter;
    }

    public double getValue(Profile profile) {
        return valueGetter.apply(profile).doubleValue();
    }

    public String format(double value) {
        return formatter.apply(value);
    }
}