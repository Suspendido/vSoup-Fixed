package kami.gg.souppvp.feats.hooks.placeholder.type;

import kami.gg.souppvp.feats.hooks.placeholder.Placeholder;
import kami.gg.souppvp.feats.hooks.placeholder.PlaceholderHook;
import org.bukkit.entity.Player;

import java.util.List;

public class NonePlaceholderHook extends PlaceholderHook implements Placeholder {

    public NonePlaceholderHook() {
        super();
    }

    @Override
    public String replace(Player player, String string) {
        return string;
    }

    @Override
    public List<String> replace(Player player, List<String> list) {
        return list;
    }
}