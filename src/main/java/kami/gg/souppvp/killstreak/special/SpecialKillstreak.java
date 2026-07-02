package kami.gg.souppvp.killstreak.special;

import kami.gg.souppvp.killstreak.ConfigurableKillstreak;
import org.bukkit.entity.Player;

/**
 * Interface for special killstreak types that have custom behavior.
 * Implementations should handle the specific logic for each special type.
 */
public interface SpecialKillstreak {

    SpecialTypeKillstreak getSpecialType();

    default String[] getDefaultLore() {
        return getSpecialType().getDescription().toArray(new String[0]);
    }

    void apply(Player player, ConfigurableKillstreak.RewardData rewardData);
}
