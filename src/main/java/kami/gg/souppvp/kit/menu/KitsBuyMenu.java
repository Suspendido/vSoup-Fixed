package kami.gg.souppvp.kit.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.button.KitButton;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KitsBuyMenu extends PaginatedMenu {

    private static final int[] CORNERS = {
            1, 2, 3, 4, 5, 6, 7, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.translate("Kits Shop");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        boolean freeMode = SoupPvP.getIsFreeKitsMode();

        int index = 0;
        for (Kit kit : SoupPvP.getInstance().getKitsHandler().getKits()) {
            if (!freeMode && !profile.getUnlockedKits().contains(kit.getName())) {
                buttons.put(index++, new KitButton(kit));
            }
        }

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

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }
}
