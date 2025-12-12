package kami.gg.souppvp.placeholder;

import com.github.llanezsa.library.placeholder.PlaceholderAdapter;
import kami.gg.souppvp.SoupPvP;
import me.activated.core.plugin.AquaCore;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SoupPlaceholderAdapter implements PlaceholderAdapter {

    public Map<String, Object> getPlaceholders(Player player, String s) {
        HashMap<String, Object> placeholders = new HashMap<String, Object>();

        placeholders.put("<map-timeleft>", SoupPvP.getInstance().getMapManager().getTimeLeft());
        placeholders.put("<map-start>", SoupPvP.getInstance().getMapManager().getStartDate());
        placeholders.put("<map-end>", SoupPvP.getInstance().getMapManager().getEndDate());

        return placeholders;
    }
}