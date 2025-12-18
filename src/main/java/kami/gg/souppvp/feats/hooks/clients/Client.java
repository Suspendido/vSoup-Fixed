package kami.gg.souppvp.feats.hooks.clients;

import org.bukkit.entity.Player;

import java.util.List;

public interface Client {

    void overrideNametags(Player target, Player viewer, List<String> tag);
    void clearNametags(Player player);
    void handleJoin(Player player);

}