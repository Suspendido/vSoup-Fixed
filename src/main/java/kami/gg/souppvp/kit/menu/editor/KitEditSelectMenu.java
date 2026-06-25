package kami.gg.souppvp.kit.menu.editor;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.button.CreateKitButton;
import kami.gg.souppvp.kit.button.KitEditButton;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KitEditSelectMenu extends PaginatedMenu {

    private static final int[] CORNERS = {
            1, 2, 3, 4, 5, 6, 7, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    public KitEditSelectMenu(Player player) {
        super(player, "Select Kit to Edit", 54);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int index = 0;
        for (Kit kit : SoupPvP.getInstance().getKitsHandler().getKits()) {
            buttons.put(index, new KitEditButton(kit));
            index++;
        }

        buttons.put(index, new CreateKitButton());

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> global = new HashMap<>();
        Button filler = getPlaceholderButton();

        for (int slot : CORNERS) {
            global.put(slot, filler);
        }

        return global;
    }
}
