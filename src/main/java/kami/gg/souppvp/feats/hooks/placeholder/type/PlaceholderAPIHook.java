package kami.gg.souppvp.feats.hooks.placeholder.type;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.hooks.placeholder.Placeholder;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class PlaceholderAPIHook implements Placeholder {

    public PlaceholderAPIHook() {
        this.load();
    }

    private void load() {
        new PlaceholderExpansion() {
            @Override
            public @NotNull String getIdentifier() {
                return "SoupPvP";
            }

            @Override
            public @NotNull String getAuthor() {
                return "hieu";
            }

            @Override
            public @NotNull String getVersion() {
                return "1.0";
            }

            @Override
            public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
                if (player == null) {
                    return null;
                }

                if (params.startsWith("map_")) {
                    String[] split = params.split("_");
                    if (split.length < 3) return null;

                    String type = split[1];

                    switch (type.toLowerCase()) {
                        case "timeleft":
                            return SoupPvP.getInstance().getMapManager().getTimeLeft();

                        case "start":
                            return SoupPvP.getInstance().getMapManager().getStartDate();

                        case "end":
                            return SoupPvP.getInstance().getMapManager().getEndDate();
                    }
                }

                return null;
            }
        }.register();
    }

    @Override
    public String replace(Player player, String string) {
        if (player == null || string == null) return "";
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    @Override
    public List<String> replace(Player player, List<String> list) {
        if (player == null || list == null) return Collections.emptyList();
        return PlaceholderAPI.setPlaceholders(player, list);
    }
}