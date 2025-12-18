package kami.gg.souppvp.feats.hooks.placeholder;

import org.bukkit.entity.Player;

import java.util.List;

public interface Placeholder {

    String replace(Player player, String string);

    List<String> replace(Player player, List<String> list);
}