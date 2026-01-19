package kami.gg.souppvp.coinflip.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.coinflip.button.CoinFlipWagerButton;
import kami.gg.souppvp.coinflip.button.CreateWagerButton;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CoinFlipMenu extends PaginatedMenu {

    private static final int[] CORNERS = {
            1, 2, 3, 4, 5, 6, 7, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&a&lCoinflip";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 19;
        for (CoinFlip coinFlip : SoupPvP.getInstance().getCoinFlipsHandler().getCoinFlips()) {
            if (i == 26) {
                i = 28;
            } else if (i == 35) {
                i = 37;
            } else if (i == 44) {
                i = buttons.size();
            }
            buttons.putIfAbsent(i++, new CoinFlipWagerButton(coinFlip));
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

        global.put(4, new CreateWagerButton());

        return global;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }
}
