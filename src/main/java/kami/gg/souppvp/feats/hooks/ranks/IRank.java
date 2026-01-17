package kami.gg.souppvp.feats.hooks.ranks;

import org.bukkit.entity.Player;

public interface IRank {

    String getRankName(Player player);

    String getRankPrefix(Player player);

    String getRankSuffix(Player player);

    String getRankColor(Player player);

    int getRankWeight(Player player);
}