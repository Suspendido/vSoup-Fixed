package kami.gg.souppvp.killstreak.special.impl;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialKillstreak;
import kami.gg.souppvp.killstreak.special.SpecialTypeKillstreak;
import kami.gg.souppvp.util.PlayerUtil;
import org.bukkit.entity.Player;

public class RepairKillstreak implements SpecialKillstreak {

    @Override
    public SpecialTypeKillstreak getSpecialType() {
        return SpecialTypeKillstreak.REPAIR;
    }

    @Override
    public void apply(Player player, ConfigurableKillstreak.RewardData rewardData) {
        PlayerUtil.repairPlayer(player);
    }
}
