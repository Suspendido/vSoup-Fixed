package kami.gg.souppvp.hooks.ranks;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.hooks.ranks.type.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class RankHook implements Rank {

    private Rank rank;

    public RankHook() {
        super();
        this.load();
    }

    private void load() {
        if (verifyPlugin("LuckPerms", SoupPvP.getInstance())) {
            rank = new LuckPermsRank();

        } else if (verifyPlugin("AquaCore", SoupPvP.getInstance())) {
            rank = new AquaCoreRank();

        } else {
            rank = new NoneRank();
        }
    }

    public static boolean verifyPlugin(String plugin, SoupPvP instance) {
        PluginManager pm = instance.getServer().getPluginManager();
        return pm.getPlugin(plugin) != null;
    }

    @Override public String getRankName(Player player) {
        return rank.getRankName(player);
    }
    @Override public String getRankPrefix(Player player) {
        return rank.getRankPrefix(player);
    }
    @Override public String getRankSuffix(Player player) {
        return rank.getRankSuffix(player);
    }
    @Override public String getRankColor(Player player) {
        return rank.getRankColor(player);
    }
    @Override public int getRankWeight(Player player) {
        return rank.getRankWeight(player);
    }
}