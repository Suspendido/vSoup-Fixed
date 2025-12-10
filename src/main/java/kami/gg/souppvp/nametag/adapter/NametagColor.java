package kami.gg.souppvp.nametag.adapter;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.nametag.extra.NameVisibility;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.bukkit.entity.Player;

public class NametagColor implements NametagAdapter {

    @Override
    public String getAndUpdate(Player player, Player target) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByName(target.getDisplayName());
        if (profile.getBounty() > 0) {
            return createTeam(player, target, "bounty", "", "&e");
        } else if (profile.isJuggernaut()) {
            return createTeam(player, target, "juggernaut", "", "&4&l");
        }

        return createTeam(player, target, "enemies", "", "&c");
    }

    private String createTeam(Player player, Player target, String name, String prefix, String color) {
        return createTeam(player, target, name, prefix, color, NameVisibility.ALWAYS);
    }

    private String createTeam(Player player, Player target, String name, String prefix, String color, NameVisibility visibility) {
        String formattedPrefix = (prefix.isEmpty() ? "" : prefix
                .replace("%rank-color%", CC.translate(SoupPvP.getInstance().getRankHook().getRankColor(target)))
                .replace("%rank-name%", CC.translate(SoupPvP.getInstance().getRankHook().getRankName(target))));

        return formattedPrefix + color;
    }
}