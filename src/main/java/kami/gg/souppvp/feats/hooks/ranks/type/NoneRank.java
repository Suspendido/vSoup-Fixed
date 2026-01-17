package kami.gg.souppvp.feats.hooks.ranks.type;

import kami.gg.souppvp.feats.hooks.ranks.IRank;
import org.bukkit.entity.Player;

public class NoneRank implements IRank {
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
