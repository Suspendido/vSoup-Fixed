package kami.gg.souppvp.feats.hooks.clients.type;

import com.cheatbreaker.api.CheatBreakerAPI;
import kami.gg.souppvp.feats.hooks.clients.Client;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class CheatBreaker implements Client {

    @Override
    public void overrideNametags(Player target, Player viewer, List<String> tag) {
        CheatBreakerAPI.getInstance().overrideNametag(target, tag, viewer);
    }

    @Override
    public void clearNametags(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CheatBreakerAPI.getInstance().resetNametag(onlinePlayer, player);
        }
    }

    @Override
    public void handleJoin(Player player) {
        CheatBreakerAPI.getInstance().setLegacyCombat(player, true);
    }

    @Override
    public void giveStaffModules(Player player) {
        CheatBreakerAPI.getInstance().giveAllStaffModules(player);
    }

    @Override
    public void disableStaffModules(Player player) {
        CheatBreakerAPI.getInstance().disableAllStaffModules(player);
    }
}
