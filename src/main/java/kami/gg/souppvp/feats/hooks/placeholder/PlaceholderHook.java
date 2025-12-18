package kami.gg.souppvp.feats.hooks.placeholder;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.hooks.placeholder.type.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.List;

public class PlaceholderHook implements Placeholder {

    private Placeholder placeholder;

    public PlaceholderHook() {
        this.load();
    }

    private void load() {
        if (verifyPlugin("PlaceholderAPI", SoupPvP.getInstance())) {
            placeholder = new PlaceholderAPIHook();

        } else {
            placeholder = new NonePlaceholderHook();
        }
    }

    @Override
    public String replace(Player player, String string) {
        return placeholder.replace(player, string);
    }

    @Override
    public List<String> replace(Player player, List<String> list) {
        return placeholder.replace(player, list);
    }

    public static boolean verifyPlugin(String plugin, SoupPvP instance) {
        PluginManager pm = instance.getServer().getPluginManager();
        return pm.getPlugin(plugin) != null;
    }

}