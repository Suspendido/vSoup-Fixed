package kami.gg.souppvp.killstreak.special.impl;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialTypeKillstreak;
import kami.gg.souppvp.profile.Profile;
import org.bukkit.entity.Player;

public class ExtraCreditsKillstreak implements SpecialKillstreak {

    @Override
    public SpecialTypeKillstreak getSpecialType() {
        return SpecialTypeKillstreak.EXTRA_CREDITS;
    }

    @Override
    public void apply(Player player, ConfigurableKillstreak.RewardData rewardData) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile != null) {
            profile.setCredits(profile.getCredits() + 1000);
        }
    }
}
