package kami.gg.souppvp.feats.hooks.clients;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.hooks.clients.type.*;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ClientHook implements Client {

    private final List<Client> clients;

    public ClientHook() {
        super();
        this.clients = new ArrayList<>();
        this.load();
    }

    private void load() {
        if (verifyPlugin("Apollo-Bukkit", SoupPvP.getInstance())) {
            clients.add(new LunarClient());
        }

        if (verifyPlugin("CheatBreakerAPI", SoupPvP.getInstance())) {
            clients.add(new CheatBreaker());
        }
    }

    public static boolean verifyPlugin(String plugin, SoupPvP instance) {
        PluginManager pm = instance.getServer().getPluginManager();
        return pm.getPlugin(plugin) != null;
    }

    @Override
    public void overrideNametags(Player target, Player viewer, List<String> tag) {
        for (Client client : clients) {
            client.overrideNametags(target, viewer, tag);
        }
    }

    @Override
    public void clearNametags(Player player) {
        for (Client client : clients) {
            client.clearNametags(player);
        }
    }

    @Override
    public void handleJoin(Player player) {
        for (Client client : clients) {
            client.handleJoin(player);
        }
    }

    @Override
    public void giveStaffModules(Player player) {
        for (Client client : clients) {
            client.giveStaffModules(player);
        }
    }

    @Override
    public void disableStaffModules(Player player) {
        for (Client client : clients) {
            client.disableStaffModules(player);
        }
    }
}