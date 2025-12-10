package kami.gg.souppvp.nametag.adapter;

import org.bukkit.entity.Player;

public interface NametagAdapter {

    String getAndUpdate(Player player, Player target);

}