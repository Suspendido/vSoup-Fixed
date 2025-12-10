package kami.gg.souppvp.hooks.ranks.type;

import kami.gg.souppvp.hooks.ranks.Rank;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.entity.Player;

public class AquaCoreRank implements Rank {
    @Override
    public String getRankName(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getName();
    }

    @Override
    public String getRankPrefix(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getPrefix();
    }

    @Override
    public String getRankSuffix(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getSuffix();
    }

    @Override
    public String getRankColor(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getColor().toString();
    }

    @Override
    public int getRankWeight(Player player) {
        return AquaCoreAPI.INSTANCE.getPlayerRank(player.getUniqueId()).getWeight();
    }
}
