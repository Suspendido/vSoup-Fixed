package kami.gg.souppvp.feats.hooks.ranks;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.hooks.ranks.type.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class IRankHook implements IRank {

    private IRank IRank;

    public IRankHook() {
        super();
        this.load();
    }

    private void load() {
        if (verifyPlugin("LuckPerms", SoupPvP.getInstance())) {
            IRank = new LuckPermsIRank();

        } else if (verifyPlugin("AquaCore", SoupPvP.getInstance())) {
            IRank = new AquaCoreRank();

        } else if (verifyPlugin("circuit-bukkit", SoupPvP.getInstance())) {
            IRank = new CirCuit();

        } else {
            IRank = new NoneRank();
        }
    }

    public static boolean verifyPlugin(String plugin, SoupPvP instance) {
        PluginManager pm = instance.getServer().getPluginManager();
        return pm.getPlugin(plugin) != null;
    }

    @Override public String getRankName(Player player) {
        return IRank.getRankName(player);
    }
    @Override public String getRankPrefix(Player player) {
        return IRank.getRankPrefix(player);
    }
    @Override public String getRankSuffix(Player player) {
        return IRank.getRankSuffix(player);
    }
    @Override public String getRankColor(Player player) {
        return IRank.getRankColor(player);
    }
    @Override public int getRankWeight(Player player) {
        return IRank.getRankWeight(player);
    }
}