package kami.gg.souppvp.killstreak.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.Killstreak;
import kami.gg.souppvp.killstreak.menu.button.KillstreakButton;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KillstreakMenu extends Menu {

    public KillstreakMenu(Player player) {
        super(player, "Killstreaks", 36, true);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        HashMap<Integer, Button> buttonHashMap = new HashMap<>();

        int i=10;
        for (Killstreak killstreak : SoupPvP.getInstance().getKillstreaksHandler().getKillstreaks()) {
            if (i == 17) {
                i = 19;
            }
            buttonHashMap.put(i, new KillstreakButton(killstreak));
            i++;
        }
        setFillEnabled(true);
        return buttonHashMap;
    }
}
