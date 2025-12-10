package kami.gg.souppvp.hooks.ranks.type;

import kami.gg.souppvp.hooks.ranks.Rank;
import org.bukkit.entity.Player;

public class NoneRank implements Rank {
    @Override
    public String getRankName(Player player) {
        return "";
    }

    @Override
    public String getRankPrefix(Player player) {
        return "";
    }

    @Override
    public String getRankSuffix(Player player) {
        return "";
    }

    @Override
    public String getRankColor(Player player) {
        return "";
    }

    @Override
    public int getRankWeight(Player player) {
        return 0;
    }
}
