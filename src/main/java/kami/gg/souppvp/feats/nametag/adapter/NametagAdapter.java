package kami.gg.souppvp.feats.nametag.adapter;

import org.bukkit.entity.Player;

public interface NametagAdapter {

    String getAndUpdate(Player player, Player target);

}